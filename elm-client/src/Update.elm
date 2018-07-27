module Update exposing (..)

import HttpActions exposing (deleteMeasure, getMeasure, getMeasureList, putMeasure)
import List.Extra exposing (unique)
import Model exposing (..)


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
                nextStepId = getNextStepId oldMeasure.steps
                newMeasure = { oldMeasure
                    | steps = oldMeasure.steps ++
                        [Step nextStepId "(select)" (nextStepId + 10) 99999 True []] }
            in
                { model | measure = newMeasure } ! []

        MeasureName name ->
            let
                oldMeasure = model.measure
                newMeasure = { oldMeasure | name = name }
            in
                { model | measure = newMeasure } ! []

        MeasureDescription description ->
            let
                oldMeasure = model.measure
                newMeasure = { oldMeasure | description = description }
            in
                { model | measure = newMeasure } ! []

        SuccessStepId idx successStepIdString ->
            let
                successStepId = String.toInt successStepIdString |> Result.toMaybe |> Maybe.withDefault 99999
                oldMeasure = model.measure
                newMeasure = updateStepAtIndex oldMeasure idx (\step -> { step | successStepId = successStepId } )
            in
                { model | measure = newMeasure } ! []

        FailureStepId idx failureStepIdString ->
            let
                failureStepId = String.toInt failureStepIdString |> Result.toMaybe |> Maybe.withDefault 99999
                oldMeasure = model.measure
                newMeasure = updateStepAtIndex oldMeasure idx (\step -> { step | failureStepId = failureStepId } )
            in
                { model | measure = newMeasure } ! []

        ParameterValue idx name value ->
            let
                 oldMeasure = model.measure
                 newMeasure = updateStepAtIndex oldMeasure idx (\step ->
                    { step | parameters = updateParameterValue step.parameters name value })
            in
                { model | measure = newMeasure } ! []

        GetMeasuresResponse (Ok measureItems) ->
            { model | measures = measureItems } ! []

        GetMeasuresResponse (Err message) ->
            let
                d = Debug.log "Error" (toString message)
            in
                model ! []

        SelectMeasure measureId ->
            model ! [ getMeasure measureId ]

        DeleteMeasure measureId ->
            model ! [ deleteMeasure measureId ]

        DeleteMeasureResponse (Ok _) ->
            let
                newMeasures = List.filter (\m -> m.id /= model.measure.id) model.measures
            in
                { model | measures = newMeasures, measure = emptyMeasure } ! []

        DeleteMeasureResponse (Err message) ->
           let
               d = Debug.log "Error" (toString message)
           in
               model ! []

        ClearMeasure ->
                { model | measure = emptyMeasure } ! []

        GetMeasureResponse (Ok measure) ->
                { model | measure = measure } ! []

        GetMeasureResponse (Err message) ->
             let
                 d = Debug.log "Error" (toString message)
             in
                 { model | measure = emptyMeasure } ! []

        GetRuleParamsResponse (Ok ruleParameters) ->
            let
                rules = List.map .ruleName ruleParameters
                uniqueSortedRules = List.sort (unique rules)
            in
                { model | ruleParameters = ruleParameters, rules = uniqueSortedRules  } ! []

        GetRuleParamsResponse (Err message) ->
             let
                 d = Debug.log "Error" (toString message)
             in
                 model ! []

        SaveMeasure measure ->
            model ! [ putMeasure measure ]

        NewMeasureResponse (Ok newMeasure) ->
            let
                d = Debug.log "MEASURE: " (toString newMeasure)

                measureIds = List.map (\m -> m.id) model.measures
                exists = List.member newMeasure.id measureIds
                newMeasures = (
                    if (exists == False) then
                        MeasureItem newMeasure.id newMeasure.name :: model.measures
                    else
                        List.map (\m ->
                            if (m.id == newMeasure.id) then
                                MeasureItem m.id newMeasure.name
                            else m) model.measures
                    )
            in
                { model | measures = newMeasures, measure = newMeasure } ! []

        NewMeasureResponse (Err message) ->
            let
                d = Debug.log "Error" (toString message)
            in
                model ! []

        SelectRule idx ruleName ->
            let
                oldMeasure = model.measure
                newStepParameters = getRuleParameters model.ruleParameters ruleName

                newMeasure = (
                    if (ruleName == "ExitMeasure") then
                        updateStepAtIndex oldMeasure idx (\step ->
                            { step | ruleName = ruleName, parameters = newStepParameters, successStepId = 99999 })
                    else
                        updateStepAtIndex oldMeasure idx (\step ->
                            { step | ruleName = ruleName, parameters = newStepParameters })
                    )
            in
                { model | measure = newMeasure } ! []


updateStepAtIndex : Measure -> Int -> (Step -> Step) -> Measure
updateStepAtIndex measure idx updateFunction =
    let
        oldStep = List.take 1 (List.drop idx measure.steps)
        updatedStep = oldStep |> List.map (updateFunction)
        updatedSteps = (List.take idx measure.steps) ++ updatedStep ++ (List.drop (idx+1) measure.steps)
    in
        { measure | steps = updatedSteps }


updateParameterValue : List RuleParameter -> String -> String -> List RuleParameter
updateParameterValue parameters paramName paramValue =
    List.map (\a -> if (a.paramName == paramName) then { a | paramValue = paramValue } else a ) parameters


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


updateStepIds : List Step -> List Step -> List Step
updateStepIds originalList revisedList =
    List.map2 reviseStepIds originalList revisedList


reviseStepIds : Step -> Step -> Step
reviseStepIds step_a step_b =
    if (step_a.stepId /= step_b.stepId) then

        {step_b |
            stepId = step_a.stepId
            , successStepId = step_a.successStepId
            , failureStepId = step_a.failureStepId}
    else
        step_b


getNextStepId : List Step -> Int
getNextStepId steps =
    let
        stepIds = List.map .stepId steps
        maxStepId = List.maximum stepIds
    in
        case maxStepId of
        Just maxStepId ->
            maxStepId + 10
        Nothing ->
            10

getRuleParameters : List RuleParameter -> String -> List RuleParameter
getRuleParameters ruleParameters ruleName =
     List.filter (\r -> r.ruleName == ruleName) ruleParameters

emptyMeasure : Measure
emptyMeasure =
    Measure 0 "" "" [] "" Nothing