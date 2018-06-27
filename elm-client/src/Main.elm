module Main exposing (..)

import Html exposing (program)
import HttpActions exposing (getMeasureList, getRules)
import Messages exposing (Msg(DragAt, DragEnd))
import Models exposing (MeasureItem, Model, Step)
import Mouse exposing (Position)
import Update exposing (..)
import View exposing (view)

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
            , name = "default measure name"
            , description = "default measure description"
            , steps = List.sortBy .stepId [ Step 0 "defaultStep" 0 0 False]
            , drag = Nothing
        }
    , measures = [ MeasureItem 0 "default measure name" ]
    , rules = [""]
    } ! [getMeasureList, getRules]


-- SUBSCRIPTIONS

subscriptions : Model -> Sub Msg
subscriptions model =
    case model.measure.drag of
        Nothing ->
            Sub.none

        Just _ ->
            Sub.batch [ Mouse.moves DragAt, Mouse.ups DragEnd ]
