module Dashboard.Subscriptions exposing (subscriptions)

import Dashboard.Message exposing (Msg)
import Dashboard.Model exposing (Model)
import WebSocket exposing (listen)

subscriptions : Model -> Sub Msg
subscriptions model =
    listen "/jobs-subscription"
