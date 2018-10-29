module Dashboard.View exposing (..)

import Html exposing (Html, h1, text)
import Dashboard.Message exposing (Msg)
import Dashboard.Model exposing (Model)

view: Model -> Html Msg
view model =
    h1[][text model.progress]
