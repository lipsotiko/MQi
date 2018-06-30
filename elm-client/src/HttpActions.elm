module HttpActions exposing (getMeasureList, getMeasure, getRules)

import Http
import Json.Decode as Json exposing (list, int, string, Decoder)
import Json.Decode.Pipeline exposing (decode, hardcoded, optional, required, requiredAt)
import Model exposing (..)

getMeasureList : Cmd Msg
getMeasureList =
    let
        url = "/measure_list"
        request = Http.get url (Json.list measureListItemDecoder)
    in
        Http.send GetMeasures request

measureListItemDecoder: Decoder MeasureItem
measureListItemDecoder = decode MeasureItem
    |> required "measureId" int
    |> required "measureName" string

getMeasure : Int -> Cmd Msg
getMeasure id =
    let
        url = String.append "/measure?id=" (toString(id))
        request = Http.get url measureDecoder
    in
        Http.send GetMeasure request

measureDecoder: Decoder Measure
measureDecoder = decode Measure
    |> required "measureId" int
    |> required "measureName" string
    |> requiredAt ["measureJson","description"] string
    |> requiredAt ["measureJson","steps"] (Json.list stepDecoder)
    |> hardcoded Nothing

stepDecoder: Decoder Step
stepDecoder = decode Step
    |> required "stepId" int
    |> required "rule" string
    |> required "successStepId" int
    |> required "failureStepId" int
    |> hardcoded False

getRules : Cmd Msg
getRules =
    let
        url = "/rules"
        request = Http.get url (Json.list Json.string)
    in
        Http.send GetRules request