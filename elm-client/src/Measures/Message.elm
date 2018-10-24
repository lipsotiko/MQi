module Measures.Message exposing (..)

import Http
import Measures.Model exposing (Measure, MeasureItem, RuleParameter)
import Mouse exposing (Position)


type Msg
    = AddStep
    | AddMeasure
    | DeleteStep Int
    | EditStep Int
    | DragStart Int Position
    | DragAt Position
    | DragEnd Position
    | MeasureName String
    | MeasureDescription String
    | SuccessStepId Int String
    | FailureStepId Int String
    | ParameterValue Int String String
    | SelectMeasure Int
    | SaveMeasure Measure
    | DeleteMeasure Int
    | SelectRule Int String
    | SelectMeasureForBatch Int
    | ProcessMeasures
    | GetMeasuresResponse (Result Http.Error (List MeasureItem))
    | GetMeasureResponse (Result Http.Error (Measure))
    | GetRuleParamsResponse (Result Http.Error (List RuleParameter))
    | DeleteMeasureResponse (Result Http.Error String)
    | NewMeasureResponse (Result Http.Error Measure)
    | ProcessMeasuresResponse (Result Http.Error (String))
