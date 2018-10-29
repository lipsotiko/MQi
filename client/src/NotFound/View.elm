module NotFound.View exposing (..)

import Html exposing (Html, h1, text)
import NotFound.Message exposing (Msg)
import NotFound.Model exposing (Model)

view: Model -> Html Msg
view model =
    h1[][text model.message]
