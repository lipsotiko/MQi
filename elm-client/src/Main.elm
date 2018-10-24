module Main exposing (..)

import Layout
import Navigation

main : Program Never Layout.Model Layout.Msg
main =
    Navigation.program Layout.OnLocationChange
        { init = Layout.init
        , update = Layout.update
        , view = Layout.view
        , subscriptions = Layout.subscriptions
        }
