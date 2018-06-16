module Models exposing (..)

type alias Model = {
    measure : Measure
    , measureList : List MeasureItem
    }

type alias Measure = {
    id : Int
    , name : String
    , description : String
    , steps : List Step
    , drag : Maybe Drag
    }

type alias Step = {
    stepId : Int
    , rule : String
    , successStepId : Int
    , failureStepId: Int
    , isEditing : Bool
    }

type alias MeasureItem = {
    id : Int
    , name : String
    }

type alias Drag = {
    itemIndex : Int
    , startY : Int
    , currentY : Int
    }
