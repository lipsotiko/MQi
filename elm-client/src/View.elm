module View exposing (..)

import Html exposing (..)
import Html.Attributes exposing (..)
import Html.Events exposing (on, onClick, onInput)
import Json.Decode
import Model exposing (..)
import Mouse exposing (Position)

view : Model -> Html Msg
view model =

    div
        [ class "container" ]
        [
            div [ class "measure-list" ] [
                h3 [ ] [text "Measures:"]
                , ul
                  [ class "list-container" ]
                  <| List.indexedMap measureItemView model.measures
            ]

            , div [ class "measure" ] [
                div []
                    [
                        h3 [ ] [text "Name:"]
                        , input [  class "measure-name"
                                   , placeholder "Measure name..."
                                   , value model.measure.name
                                   , onInput MeasureName][]
                    ]

                , div
                    []
                    [
                        h3 [ ] [text "Description:"]
                        , textarea [ class "measure-desc"
                                    , placeholder "Describe this measure..."
                                    , value model.measure.description
                                    , onInput MeasureDescription][]
                    ]

               , div
                    []
                    [
                        h3 [ ]
                        [ text "Steps:" ]
                        , button
                            [ onClick AddStep]
                            [text "+"]
                    ]
                , ul
                    [ class "list-container" ]
                    <| List.indexedMap (stepView model) model.measure.steps
                , button [ onClick (SaveMeasure model.measure)] [ text "Save" ]
                , button [ onClick (DeleteMeasure model.measure.id)] [ text "Delete" ]
                , button [ onClick (SelectMeasure model.measure.id) ] [ text "Reset"]
                , button [ onClick ClearMeasure ] [ text "Clear" ]
                , p [][text model.measure.lastUpdated]
                , p [][text model.measure.minimumSystemVersion]
                , p [][text (toString model.measure.traceRules)]
            ]
        ]

measureItemView: Int -> MeasureItem -> Html Msg
measureItemView idx measureItem =
    li [ class "list-item", onClick (SelectMeasure measureItem.id)][
       text measureItem.name
    ]

stepView : Model -> Int -> Step -> Html Msg
stepView model idx step =
    let
        showStyle =
            if step.isEditing then "show"
            else "hide"

        stepStyle =
            if step.isEditing then "expanded list-item"
            else "list-item"

        editSaveStepText =
            if step.isEditing then "Close"
                else "Edit"

        moveStyle =
            case model.measure.drag of
                Just { itemIndex, startY, currentY } ->
                    if itemIndex == idx then
                        [ ( "transform", "translateY( " ++ toString (currentY - startY) ++ "px) translateZ(10px)" )
                        , ( "box-shadow", "0 3px 6px rgba(0,0,0,0.24)" )
                        , ( "willChange", "transform" )
                        ]
                    else
                        []

                Nothing ->
                    []

        makingWayStyle =
            case model.measure.drag of
                Just { itemIndex, startY, currentY } ->
                    if (idx < itemIndex) && (currentY - startY) < (idx - itemIndex) * 42 + 20 then
                        [ ( "transform", "translateY(42px)" )
                        , ( "transition", "transform 200ms ease-in-out" )
                        ]
                    else if (idx > itemIndex) && (currentY - startY) > (idx - itemIndex) * 42 - 20 then
                        [ ( "transform", "translateY(-42px)" )
                        , ( "transition", "transform 200ms ease-in-out" )
                        ]
                    else if idx /= itemIndex then
                        [ ( "transition", "transform 200ms ease-in-out" ) ]
                    else
                        []

                Nothing ->
                    []

    in
        li [ class stepStyle, style <| moveStyle ++ makingWayStyle ]
            [
                div[ class "step-header" ][
                    label [][text (toString step.stepId)]
                    , select [value step.ruleName, onInput (SelectRule idx)]
                        (List.map (\a -> option [value a, selected (a == step.ruleName)][text a]) ("(select)"::model.rules))
                    , input [ maxlength 5, value (toString step.successStepId), placeholder "###", onInput (SuccessStepId idx)][]
                    , input [ maxlength 5, value (toString step.failureStepId), placeholder "###", onInput (FailureStepId idx)][]
                    , button [ class showStyle, onClick (DeleteStep idx) ] [ text "Delete" ]
                    , button [ onClick (EditStep idx) ][ text editSaveStepText ]
                    , button [ class "drag-btn", onMouseDown <| DragStart idx ] [ text "Drag" ]
                   ]
                , div [ class "step-parameters", class showStyle ]
                    (parametersView idx step.parameters)
            ]


filterByRuleName : List RuleParameter -> String -> List RuleParameter
filterByRuleName ruleParameters ruleName =
    List.filter (\r -> r.ruleName == ruleName) ruleParameters


parametersView : Int -> List RuleParameter -> List (Html Msg)
parametersView idx ruleParameters  =
    List.map (\a -> parameterView idx a) ruleParameters


parameterView : Int -> RuleParameter -> Html Msg
parameterView idx ruleParameters =
    let
        inputBox = (
            if (ruleParameters.paramType == "INTEGER") then
                input [value ruleParameters.paramValue, placeholder "###", onInput (ParameterValue idx ruleParameters.paramName)][]
            else if (ruleParameters.paramType == "BOOLEAN") then
                input [value ruleParameters.paramValue, placeholder "TRUE or FALSE", onInput (ParameterValue idx ruleParameters.paramName)][]
            else if (ruleParameters.paramType == "DATE") then
                input [value ruleParameters.paramValue, placeholder "YYYYMMDD", onInput (ParameterValue idx ruleParameters.paramName)][]
            else if (ruleParameters.paramType == "TEXT") then
                input [value ruleParameters.paramValue, placeholder "Free text field", onInput (ParameterValue idx ruleParameters.paramName)][]
            else if (ruleParameters.paramType == "INVISIBLE") then
                 div[][]
            else
                input [value ruleParameters.paramValue, placeholder ruleParameters.paramType, onInput (ParameterValue idx ruleParameters.paramName)][]
            )

    in
        div [ class "parameter-item"][
                label[][text ruleParameters.paramName]
                , inputBox
        ]


onMouseDown : (Position -> msg) -> Attribute msg
onMouseDown msg =
    on "mousedown" (Json.Decode.map msg Mouse.position)
