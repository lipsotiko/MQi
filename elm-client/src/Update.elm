module Update exposing (..)

import HttpActions exposing (getMeasure)
import Messages exposing (..)
import Models exposing (..)


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of

        DeleteStep idx ->
            let
                oldMeasure = model.measure
                stepsWithRemovedStepIdx = List.append (List.take idx oldMeasure.steps) (List.drop (idx+1) oldMeasure.steps)
                stepsWithUpdatedStepIds = updateStepIds oldMeasure.steps stepsWithRemovedStepIdx
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

        SuccessStepId idx successStepIdString ->
            let
                successStepId = String.toInt successStepIdString |> Result.toMaybe |> Maybe.withDefault 99999
                oldMeasure = model.measure
                stepToUpdate = List.take 1 (List.drop idx oldMeasure.steps)
                updatedStep = stepToUpdate |> List.map (\step -> { step | successStepId = successStepId } )
                updatedSteps = (List.take idx oldMeasure.steps) ++ updatedStep ++ (List.drop (idx+1) oldMeasure.steps)
                newMeasure = {oldMeasure | steps = updatedSteps}
            in
                { model | measure = newMeasure } ! []

        FailureStepId idx failureStepIdString ->
            let
                failureStepId = String.toInt failureStepIdString |> Result.toMaybe |> Maybe.withDefault 99999
                oldMeasure = model.measure
                stepToUpdate = List.take 1 (List.drop idx oldMeasure.steps)
                updatedStep = stepToUpdate |> List.map (\step -> { step | failureStepId = failureStepId } )
                updatedSteps = (List.take idx oldMeasure.steps) ++ updatedStep ++ (List.drop (idx+1) oldMeasure.steps)
                newMeasure = {oldMeasure | steps = updatedSteps}
            in
                { model | measure = newMeasure } ! []

        GetMeasures (Ok measures) ->
            { model | measures = measures } ! []

        GetMeasures (Err _) ->
            let
                d = Debug.crash "Could not retrieve the measures"
            in
                model ! []

        SelectMeasure id ->
            model ! [ getMeasure id ]

        GetMeasure (Ok measure) ->
                { model | measure = measure } ! []

        GetMeasure (Err _) ->
             let
                 d = Debug.crash "Could not retrieve the measure"
             in
                 model ! []

        GetRules (Ok rules) ->
                { model | rules = rules } ! []

        GetRules (Err _) ->
             let
                 d = Debug.crash "Could not retrieve the rules"
             in
                 model ! []


toggleStepIsEditing : Step -> Step
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

        reorderedSteps =
            List.take (fromPos + offset) listWithoutMovedStep
                    ++ movedStep
                    ++ List.drop (fromPos + offset) listWithoutMovedStep

    in
        updateStepIds steps reorderedSteps


updateStepIds: List Step -> List Step -> List Step
updateStepIds originalList revisedList =
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
