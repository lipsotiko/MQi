module Messages exposing (..)

import Http
import Models exposing (Measure, MeasureItem)
import Mouse exposing (Position)

type Msg
    = AddStep
    | DeleteStep Int
    | EditStep Int
    | DragStart Int Position
    | DragAt Position
    | DragEnd Position
    | Description String
    | Name String
    | SuccessStepId Int String
    | FailureStepId Int String
    | SelectMeasure Int
    | GetMeasures (Result Http.Error (List MeasureItem))
    | GetMeasure (Result Http.Error (Measure))
    | GetRules (Result Http.Error (List String))
