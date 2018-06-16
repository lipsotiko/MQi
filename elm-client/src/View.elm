module View exposing (..)

import Html exposing (..)
import Html.Attributes exposing (..)
import Html.Events exposing (on, onClick, onInput)
import Json.Decode
import Messages exposing (..)
import Models exposing (..)
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
                  <| List.indexedMap measureItemView model.measureList
            ]

            , div [ class "measure" ] [
                div []
                    [
                        h3 [ ] [text "Name:"]
                        , input [  class "measure-name"
                                   , placeholder "Measure name..."
                                   , value model.measure.name
                                   , onInput Name][]
                    ]

                , div
                    []
                    [
                        h3 [ ] [text "Description:"]
                        , textarea [ class "measure-desc"
                                    , placeholder "Describe this measure..."
                                    , value model.measure.description
                                    , onInput Description][]
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
                , button [] [ text "Save" ]
                , button [] [ text "Delete" ]
            ]
        ]

measureItemView: Int -> MeasureItem -> Html Msg
measureItemView idx measureItem =
    li [ class "measure list-item", onClick (SelectMeasure measureItem.id)][
       text measureItem.name
    ]

stepView : Model -> Int -> Step -> Html Msg
stepView model idx item =
    let
        deleteBtnStyle =
            if item.isEditing then "show"
            else "hide"

        stepStyle =
            if item.isEditing then "expanded list-item"
            else "list-item"

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
            [ input [ maxlength 5, value (toString item.stepId)][]
            , div [][text item.rule]
            , input [ maxlength 5, value (toString item.successStepId)][]
            , input [ maxlength 5, value (toString item.failureStepId)][]
            , button [ class deleteBtnStyle, onClick (DeleteStep idx) ] [ text "Delete" ]
            , button [ onClick (EditStep idx) ][ text "Edit" ]
            , div [ class "drag-btn", onMouseDown <| DragStart idx ] [ ]
            ]

onMouseDown : (Position -> msg) -> Attribute msg
onMouseDown msg =
    on "mousedown" (Json.Decode.map msg Mouse.position)
