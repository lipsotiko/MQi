module Layout exposing (init, update, view, subscriptions, Msg(OnLocationChange), Model)

import Html exposing (Html, a, div, h1, text)
import Html.Attributes exposing (href)
import Navigation exposing (Location)
import Route exposing (Route(DashboardRoute, MeasuresRoute, NotFoundRoute), parseLocation)
import SubPage


type alias Model = {
    subModel: SubPage.Model,
    route: Route
    }


type Msg
    = OnLocationChange Location
     | SubMsg SubPage.Msg


init : Location -> ( Model, Cmd Msg )
init location =
    let
        currentRoute = Route.parseLocation location
        (subModel, subCmd) = SubPage.init currentRoute
    in
        Model subModel currentRoute ! [Cmd.map SubMsg subCmd]


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        OnLocationChange location ->
            let
                newRoute = parseLocation location
                ( newSubmodel, cmd ) =  SubPage.init newRoute
            in
                ( { model | route = newRoute, subModel = newSubmodel }, Cmd.map SubMsg cmd )

        SubMsg cmdMsg ->
            let
                (newSubmodel, subCmd) = SubPage.update cmdMsg model.subModel
            in
                ( { model | subModel = newSubmodel }, Cmd.map SubMsg subCmd )


view : Model -> Html Msg
view model =
    let
        navigation = div[][
                         a[href "/"][text "Dashboard"],
                         a[href "/#measures"][text "Measures"]
                     ]
        content = Html.map SubMsg (SubPage.view model.subModel)
        footer = div[][text "Footer will go here..."]
    in
        div[][navigation, content, footer]


subscriptions : Model -> Sub Msg
subscriptions model =
    Sub.map SubMsg (SubPage.subscriptions model.subModel)

