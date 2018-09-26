module HttpActions exposing (processMeasures, getMeasureList, getMeasure , getRulesParams, putMeasure, deleteMeasure)

import Array exposing (Array, fromList)
import Http
import Json.Decode as Decode exposing (list, int, string, bool, Decoder)
import Json.Decode.Pipeline exposing (decode, hardcoded, optional, required, requiredAt)
import Json.Encode as Encode
import Model exposing (..)


deleteMeasure: Int -> Cmd Msg
deleteMeasure measureId =
    let
        url = String.append "/measure?measureId=" (toString measureId)
        request = delete url
    in
        Http.send DeleteMeasureResponse request


getMeasure : Int -> Cmd Msg
getMeasure measureId =
    let
        url = String.append "/measure?measureId=" (toString measureId)
        request = Http.get url measureDecoder
    in
        Http.send GetMeasureResponse request


putMeasure : Measure -> Cmd Msg
putMeasure measure =
    let
        body = Http.jsonBody (measureEncoder measure)
        request = put "/measure" body measureDecoder
    in
        Http.send NewMeasureResponse request


put : String -> Http.Body -> Decoder a -> Http.Request (a)
put url body decoder =
  Http.request {
    method = "PUT"
    , headers = []
    , url = url
    , body = body
    , expect = Http.expectJson decoder
    , timeout = Nothing
    , withCredentials = False
    }

delete : String -> Http.Request String
delete url =
  Http.request {
    method = "DELETE"
    , headers = []
    , url = url
    , body = Http.emptyBody
    , expect = Http.expectString
    , timeout = Nothing
    , withCredentials = False
    }

post : String -> Http.Body -> Http.Request (String)
post url body =
  Http.request {
    method = "POST"
    , headers = []
    , url = url
    , body = body
    , expect = Http.expectString
    , timeout = Nothing
    , withCredentials = False
    }

processMeasures : List Int -> Cmd Msg
processMeasures ids =
    let
        body = Http.jsonBody (processEncoder ids)
        request = post "/process" body
    in
        Http.send ProcessMeasuresResponse request

getMeasureList : Cmd Msg
getMeasureList =
    let
        url = "/measure_list"
        request = Http.get url (Decode.list measureListItemDecoder)
    in
        Http.send GetMeasuresResponse request

measureListItemDecoder: Decoder MeasureItem
measureListItemDecoder = decode MeasureItem
    |> required "measureId" int
    |> required "measureName" string
    |> hardcoded False


measureDecoder : Decoder Measure
measureDecoder = decode Measure
    |> required "measureId" int
    |> required "measureName" string
    |> requiredAt ["measureLogic","description"] string
    |> requiredAt ["measureLogic","steps"] (Decode.list stepDecoder)
    |> requiredAt ["measureLogic","minimumSystemVersion"] string
    |> requiredAt ["lastUpdated"] string
    |> hardcoded Nothing


measureEncoder: Measure -> Encode.Value
measureEncoder measure =
    Encode.object [
        ("measureId", Encode.int measure.id)
        , ("measureName", Encode.string measure.name)
        , ("measureLogic", (
            Encode.object [
                ("description", Encode.string measure.description)
                , ("steps", stepsEncoder measure.steps)
            ])
          )
    ]


processEncoder : List Int -> Encode.Value
processEncoder ids =
    Encode.list (List.map (\s -> Encode.int s) ids)


stepsEncoder : List Step -> Encode.Value
stepsEncoder steps =
    let
        listOfEncodedValues = List.map (\s ->
                Encode.object [
                    ("stepId", Encode.int s.stepId)
                    , ("ruleName", Encode.string s.ruleName)
                    , ("parameters", parametersEncoder s.parameters)
                    , ("successStepId", Encode.int s.successStepId)
                    , ("failureStepId", Encode.int s.failureStepId)
                ]
            ) steps
    in
        Encode.array (fromList listOfEncodedValues)


parametersEncoder : List RuleParameter -> Encode.Value
parametersEncoder parameters =
    let
        listOfEncodedValues = List.map (\p ->
                Encode.object [
                    ("paramName", Encode.string p.paramName)
                    , ("paramValue", Encode.string p.paramValue)
                    , ("paramType", Encode.string p.paramType)
                ]
            ) parameters
    in
        Encode.array (fromList listOfEncodedValues)


parameterEncoder : RuleParameter -> Encode.Value
parameterEncoder ruleParameter =
    Encode.object [
        ("paramName", Encode.string ruleParameter.paramName)
        , ("paramValue", Encode.string ruleParameter.paramValue)
        , ("paramType", Encode.string ruleParameter.paramType)
    ]


stepDecoder : Decoder Step
stepDecoder = decode Step
    |> required "stepId" int
    |> required "ruleName" string
    |> required "successStepId" int
    |> required "failureStepId" int
    |> hardcoded False
    |> required "parameters" (Decode.list ruleParamDecoder)


getRulesParams : Cmd Msg
getRulesParams =
    let
        url = "/rules_params"
        request = Http.get url (Decode.list ruleParamDecoder)
    in
        Http.send GetRuleParamsResponse request


ruleParamDecoder : Decoder RuleParameter
ruleParamDecoder = decode RuleParameter
    |> optional "ruleParamId" int 0
    |> optional "ruleName" Decode.string ""
    |> optional "paramName" Decode.string ""
    |> optional "paramType" Decode.string ""
    |> optional "paramValue" Decode.string ""
