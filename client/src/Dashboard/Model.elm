module Dashboard.Model exposing (..)

type alias Model = { progress: String }

initialDashboardModel : Model
initialDashboardModel = Model "Measures progress to be displayed here..."