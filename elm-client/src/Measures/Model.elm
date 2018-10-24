module Measures.Model exposing (..)

type alias Model = {
    measure : Measure
    , measures : List MeasureItem
    , rules : List String
    , ruleParameters : List RuleParameter
    , selectedMeasureIds : List Int
    }

type alias Measure = {
    id : Int
    , name : String
    , description : String
    , steps : List Step
    , minimumSystemVersion : String
    , lastUpdated : String
    , drag : Maybe Drag
    }

type alias Step = {
    stepId : Int
    , ruleName : String
    , successStepId : Int
    , failureStepId: Int
    , isEditing : Bool
    , parameters : List RuleParameter
    }

type alias RuleParameter = {
    ruleParamId : Int
    , ruleName : String
    , paramName : String
    , paramType : String
    , paramValue : String
    }

type alias MeasureItem = {
    id : Int
    , name : String
    , selected: Bool
    }

type alias Drag = {
    itemIndex : Int
    , startY : Int
    , currentY : Int
    }

initialMeasuresModel : Model
initialMeasuresModel = { measure = {
                           id = 0
                           , name = ""
                           , description = ""
                           , steps = []
                           , minimumSystemVersion = ""
                           , lastUpdated = ""
                           , drag = Nothing
                       }
                   , measures = []
                   , rules = []
                   , ruleParameters = []
                   , selectedMeasureIds = []
                   }

