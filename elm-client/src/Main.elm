module Main exposing (..)

import Html exposing (program)
import HttpActions exposing (getMeasureList, getRulesParams)
import Model exposing (..)
import Mouse exposing (Position)
import Update exposing (..)
import View exposing (view)

--https://exercism.io/tracks/kotlin

main : Program Never Model Msg
main =
    Html.program
        { init = init
        , view = view
        , update = update
        , subscriptions = subscriptions }

init : ( Model, Cmd Msg )
init =
    { measure = {
            id = 0
            , name = ""
            , description = ""
            , steps = []
            , minimumSystemVersion = ""
            , traceRules = False
            , lastUpdated = ""
            , drag = Nothing
        }
    , measures = []
    , rules = []
    , ruleParameters = []
    , selectedMeasureIds = []
    } ! [getMeasureList, getRulesParams]


-- SUBSCRIPTIONS

subscriptions : Model -> Sub Msg
subscriptions model =
    case model.measure.drag of
        Nothing ->
            Sub.none

        Just _ ->
            Sub.batch [ Mouse.moves DragAt, Mouse.ups DragEnd ]
