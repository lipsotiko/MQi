module Main exposing (..)

import Html exposing (..)
import Html.Attributes exposing (..)
import Html.Events exposing (on, onClick, onInput)
import Mouse exposing (Position)
import Styles
import Http
import Json.Decode as Json exposing (int, at, string, list, map)

main =
    Html.program
        { init = init
        , view = view
        , update = update
        , subscriptions = subscriptions}

-- MODEL

type alias Model = {
    measure : Measure
    , measureList : List MeasureItem
    }

type alias Measure = {
    name : String
    , description : String
    , steps : List Step
    , drag : Maybe Drag
    }

type alias Step = {
    stepId : Int
    , rule : String
    , successStepId : Int
    , failureStepId: Int
    , isEditing : Bool
    }

type alias MeasureItem = {
    id : Int
    , name : String
    }

type alias Drag = {
    itemIndex : Int
    , startY : Int
    , currentY : Int
    }

init : ( Model, Cmd Msg )
init =
    { measure = {
            name = "Sample measure name"
            , description = "Sample Measure to Build the GUI"
            , steps = List.sortBy .stepId measureSteps
            , drag = Nothing
        }
    , measureList = [ MeasureItem 0 "" ]
    } ! [getMeasureList]

measureSteps =
    [ Step 100 "Original Step 1" 200 400 False
    , Step 200 "Original Step 2" 300 400 False
    , Step 300 "Original Step 3" 400 400 False
    , Step 400 "Original Step 4" 500 99999 False
    , Step 500 "Original Step 5" 600 99999 False
    , Step 600 "Original Step 6" 99999 99999 False]


-- UPDATE

type Msg
    = AddStep
    | DeleteStep Int
    | EditStep Int
    | DragStart Int Position
    | DragAt Position
    | DragEnd Position
    | Description String
    | Name String
    | GetMeasureList (Result Http.Error (List MeasureItem))


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of

        DeleteStep idx ->
            let
                oldMeasure = model.measure
                stepsWithRemovedStepIdx = List.append (List.take idx oldMeasure.steps) (List.drop (idx+1) oldMeasure.steps)
                stepsWithUpdatedStepIds = updateListStepIds oldMeasure.steps stepsWithRemovedStepIdx
                newMeasure = {oldMeasure | steps = stepsWithUpdatedStepIds}
            in
                { model | measure = newMeasure } ! []

        EditStep idx ->
            let
                oldMeasure = model.measure
                stepToUpdate = List.take 1 (List.drop idx oldMeasure.steps)
                updatedStep = List.map toggleStepIsEditing stepToUpdate
                updatedSteps = (List.take idx oldMeasure.steps) ++ updatedStep ++ (List.drop (idx+1) oldMeasure.steps)
                newMeasure = {oldMeasure | steps = updatedSteps}
            in
               { model | measure = newMeasure } ! []

        DragStart idx pos ->
            let
                oldMeasure = model.measure
                newMeasure = { oldMeasure | drag = Just <| Drag idx pos.y pos.y}
            in
                { model | measure = newMeasure } ! []

        DragAt pos ->
            let
                oldMeasure = model.measure
                newMeasure = { oldMeasure | drag = Maybe.map (\{ itemIndex, startY } -> Drag itemIndex startY pos.y) oldMeasure.drag}
            in
                 { model | measure = newMeasure } ! []

        DragEnd pos ->
            let
                oldMeasure = model.measure

                newMeasure = case oldMeasure.drag of
                    Just { itemIndex, startY, currentY } ->
                        { oldMeasure
                            | steps =
                                moveStep
                                    itemIndex
                                    ((currentY - startY
                                        + if currentY < startY then
                                            -20
                                          else
                                            20
                                     )
                                        // 50
                                    )
                                    oldMeasure.steps
                            , drag = Nothing
                        }

                    Nothing ->
                        { oldMeasure
                            | drag = Nothing
                        }
            in
                { model | measure = newMeasure } ! []

        AddStep ->
            let
                oldMeasure = model.measure
                newMeasure = { oldMeasure
                    | steps = oldMeasure.steps ++
                        [Step (getNextStepId oldMeasure.steps) "Added Step" 99999 99999 False] }
            in
                { model | measure = newMeasure } ! []

        Name name ->
            let
                oldMeasure = model.measure
                newMeasure = { oldMeasure | name = name }
            in
                { model | measure = newMeasure } ! []

        Description description ->
            let
                oldMeasure = model.measure
                newMeasure = { oldMeasure | description = description }
            in
                { model | measure = newMeasure } ! []

        GetMeasureList (Ok measureList) ->
            { model | measureList = measureList } ! []

        GetMeasureList (Err _) ->
            let
                d = Debug.crash "Could not retrieve the measure list"
            in
                model ! []



toggleStepIsEditing: Step -> Step
toggleStepIsEditing step =
   if (step.isEditing == True) then
      { step | isEditing = False }
   else
      { step | isEditing = True }


moveStep : Int -> Int -> List Step -> List Step
moveStep fromPos offset steps =
    let
        listWithoutMovedStep =
            List.take fromPos steps ++ List.drop (fromPos + 1) steps

        movedStep =
            List.take 1 <| List.drop fromPos steps

        listWithRevisedOrder =
            List.take (fromPos + offset) listWithoutMovedStep
                    ++ movedStep
                    ++ List.drop (fromPos + offset) listWithoutMovedStep

    in
        updateListStepIds steps listWithRevisedOrder


updateListStepIds: List Step -> List Step -> List Step
updateListStepIds originalList revisedList =
    List.map2 reviseStepIds originalList revisedList

reviseStepIds: Step -> Step -> Step
reviseStepIds step_a step_b =
    if (step_a.stepId /= step_b.stepId) then

        {step_b |
            stepId = step_a.stepId
            , successStepId = step_a.successStepId
            , failureStepId = step_a.failureStepId}
    else
        step_b

getNextStepId: List Step -> Int
getNextStepId steps =
    let
        stepIds = List.map .stepId steps
        maxStepId = List.maximum stepIds
    in
        case maxStepId of
        Just maxStepId ->
            maxStepId + 100
        Nothing ->
            100



-- SUBSCRIPTIONS

subscriptions : Model -> Sub Msg
subscriptions model =
    case model.measure.drag of
        Nothing ->
            Sub.none

        Just _ ->
            Sub.batch [ Mouse.moves DragAt, Mouse.ups DragEnd ]



-- VIEW

view : Model -> Html Msg
view model =

    div
        [ style Styles.pageContainer ]
        [
            div
                [ style Styles.fldContainer ]
                [
                    h3 [style Styles.headerTitle] [text "Name:"]
                    , input [  style Styles.titleTextBox
                               , placeholder "Measure name..."
                               , value model.measure.name
                               , onInput Name][]
                ]

            , div
                [ style Styles.fldContainer ]
                [
                    h3 [style Styles.headerTitle] [text "Description:"]
                    , textarea [  style Styles.descriptionTextBox
                                , placeholder "Describe this measure..."
                                , value model.measure.description
                                , onInput Description][]
                ]

           , div
                [ style Styles.fldContainer ]
                [ h3
                    [ style Styles.headerTitle ]
                    [ text "Steps:" ]
                    , button
                        [ style Styles.addBtn
                        , onClick AddStep]
                        [text "Add"]
                ]
            , ul
                [ style Styles.listContainer ]
                <| List.indexedMap (itemView model) model.measure.steps
            , button [style Styles.saveBtn] [ text "Save" ]
        ]

itemView : Model -> Int -> Step -> Html Msg
itemView model idx item =
    let
        deleteButtonStyle =
            if item.isEditing then Styles.showBtn
            else Styles.hideBtn

        stepStyle =
            if item.isEditing then Styles.expandedListItem
            else Styles.listItem

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
        li [ style <| stepStyle ++ moveStyle ++ makingWayStyle ]
            [ input [style Styles.itemTextBox, maxlength 5, value (toString item.stepId)][]
            , div [ style Styles.itemText ][ text item.rule]
            , input [style Styles.itemTextBox, maxlength 5, value (toString item.successStepId)][]
            , input [style Styles.itemTextBox, maxlength 5, value (toString item.failureStepId)][]
            , button [ style deleteButtonStyle, onClick (DeleteStep idx) ] [ text "Delete" ]
            , button [ style Styles.editBtn, onClick (EditStep idx) ][ text "Edit" ]
            , a [ style Styles.dragBtn, onMouseDown <| DragStart idx ] [ ]
            ]

onMouseDown : (Position -> msg) -> Attribute msg
onMouseDown msg =
    on "mousedown" (Json.map msg Mouse.position)

getMeasureList : Cmd Msg
getMeasureList =
    let
        url = "http://localhost:8080/measure_list"
        request = Http.get url decodeMeasureList
    in
        Http.send GetMeasureList request

decodeMeasureList : Json.Decoder (List MeasureItem)
decodeMeasureList =
    Json.list measureListItemDecoder

measureListItemDecoder: Json.Decoder MeasureItem
measureListItemDecoder = Json.map2 MeasureItem (Json.at ["measureId"] Json.int) (Json.at ["measureName"] Json.string)