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
    | GetMeasureList (Result Http.Error (List MeasureItem))
    | SelectMeasure Int
    | GetMeasure (Result Http.Error (Measure))
