module Measures.Subscriptions exposing (subscriptions)

import Measures.Message exposing (Msg(DragAt, DragEnd))
import Measures.Model exposing (Model)
import Mouse

subscriptions : Model -> Sub Msg
subscriptions model =
    case model.measure.drag of
        Nothing ->
            Sub.none

        Just _ ->
            Sub.batch [ Mouse.moves DragAt, Mouse.ups DragEnd ]