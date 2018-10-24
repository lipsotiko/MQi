module Route exposing (Route(..), parseLocation)

import Navigation exposing (Location)
import UrlParser exposing (Parser, map, oneOf, parseHash, s, top)

type Route
    = HomeRoute
    | MeasuresRoute
    | NotFoundRoute

parseLocation : Location -> Route
parseLocation location =
    case (parseHash matchers location) of
        Just route ->
            route

        Nothing ->
            NotFoundRoute


matchers : Parser (Route -> a) a
matchers =
    oneOf
        [ map HomeRoute top
        , map MeasuresRoute (s "measures")
        ]
