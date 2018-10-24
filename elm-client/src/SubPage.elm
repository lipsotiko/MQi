module SubPage exposing (init, update, view, subscriptions, Model(..), Msg(..))

import Html exposing (Html, h1, text)
import Measures.HttpActions exposing (getMeasureList, getRulesParams)
import Measures.Model as Measures exposing (initialMeasuresModel)
import Measures.Message as MeasuresMessage
import Measures.Update as MeasuresUpdate
import Measures.View as MeasuresView
import Measures.Subscriptions as MeasuresSubscriptions
import NotFound.Model as NotFoundModel exposing (initialNotFoundModel)
import NotFound.Message as NotFoundMessage
import NotFound.View as NotFoundView
import Route exposing (Route(..))


type Model = MeasuresModel Measures.Model
            | NotFoundModel NotFoundModel.Model

type Msg = MeasuresMessage MeasuresMessage.Msg
            | NotFoundMessage NotFoundMessage.Msg


init : Route -> ( Model, Cmd Msg )
init route =
    case route of
        HomeRoute ->
            NotFoundModel initialNotFoundModel ! []

        MeasuresRoute ->
            superDupleWrap ( MeasuresModel, MeasuresMessage ) <|
            initialMeasuresModel ! [getMeasureList, getRulesParams]

        NotFoundRoute ->
            NotFoundModel initialNotFoundModel ! []


update : Msg -> Model -> (Model, Cmd Msg)
update msg model =
    case (msg, model) of
        (MeasuresMessage m, MeasuresModel mdl) ->
            superDupleWrap ( MeasuresModel, MeasuresMessage ) <|
                MeasuresUpdate.update m mdl
        (NotFoundMessage _, NotFoundModel _) ->
                (model, Cmd.none)
        unknown ->
            (model, Cmd.none)


view: Model -> Html Msg
view model =
    case model of
        MeasuresModel m ->
            Html.map MeasuresMessage (MeasuresView.view m)
        NotFoundModel m ->
            Html.map NotFoundMessage (NotFoundView.view m)


subscriptions: Model -> Sub Msg
subscriptions model =
    case model of
        MeasuresModel m ->
            Sub.map MeasuresMessage (MeasuresSubscriptions.subscriptions m)
        NotFoundModel _ ->
            Sub.map NotFoundMessage (Sub.none)


superDupleWrap : ( a -> b, c -> d ) -> ( a, Cmd c ) -> ( b, Cmd d )
superDupleWrap ( modelFunc, msgFunc ) ( model, msg ) =
    ( modelFunc model, Cmd.map msgFunc msg )
