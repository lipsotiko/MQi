module Model exposing (..)
import Http
import Mouse exposing (Position)

type alias Model = {
    measure : Measure
    , measures : List MeasureItem
    , rules : List String
    , ruleParameters : List RuleParameter
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
    }

type alias Drag = {
    itemIndex : Int
    , startY : Int
    , currentY : Int
    }

type Msg
    = AddStep
    | DeleteStep Int
    | EditStep Int
    | DragStart Int Position
    | DragAt Position
    | DragEnd Position
    | MeasureName String
    | MeasureDescription String
    | SuccessStepId Int String
    | FailureStepId Int String
    | ParameterValue Int String String
    | SelectMeasure Int
    | GetMeasures (Result Http.Error (List MeasureItem))
    | GetMeasure (Result Http.Error (Measure))
    | GetRules (Result Http.Error (List String))
    | GetRuleParams (Result Http.Error (List RuleParameter))
    | SelectRule Int String
