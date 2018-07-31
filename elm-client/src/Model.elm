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
    , minimumSystemVersion : String
    , traceRules : Bool
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
    | ClearMeasure
    | GetMeasuresResponse (Result Http.Error (List MeasureItem))
    | GetMeasureResponse (Result Http.Error (Measure))
    | GetRuleParamsResponse (Result Http.Error (List RuleParameter))
    | SaveMeasure Measure
    | DeleteMeasure Int
    | DeleteMeasureResponse (Result Http.Error String)
    | NewMeasureResponse (Result Http.Error Measure)
    | SelectRule Int String
