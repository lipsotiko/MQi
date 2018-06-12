module Styles exposing (..)
{-| Static styles used in elm-sortable-table
-}

type alias StyleList = List (String, String)


-- for page container (root element)
pageContainer : StyleList
pageContainer =
    [ ("width","50rem")
    , ("margin","auto")
    , ("padding","0 1rem 0 0")
    , ("backgroundColor","#fafafa")
    , ("fontFamily","Verdana")
    ]

-- for list header (with title and toggle button)
fldContainer : StyleList
fldContainer =
    [("padding","1rem 0 0 1rem")
    , ("margin-bottom","0")
    ]

-- for title in header
headerTitle : StyleList
headerTitle =
    [("display", "inline")
    ,("vertical-align", "middle")
    ]

-- for list container (ul)
listContainer : StyleList
listContainer =
    [ ("transformStyle","preserve-3d")
    , ("margin","0rem")
    , ("padding","0")
    ]

-- for list item (li)
listItem : StyleList
listItem =
  [ ("listStyleType","none")
  , ("margin","8px 0px 8px 16px")
  , ("height","2rem")
  , ("backgroundColor", "white")
  , ("border","1px solid rgba(0,0,0,.27)")
  , ("border-radius","2px")
  , ("box-shadow", "0 1px 2px rgba(0,0,0,0.24)")
  , ("display","flex")
  , ("justify-content","space-between")
  ]

expandedListItem : StyleList
expandedListItem =
  [ ("listStyleType","none")
  , ("margin","8px 0px 8px 16px")
  , ("height","8rem")
  , ("backgroundColor", "white")
  , ("border","1px solid rgba(0,0,0,.27)")
  , ("border-radius","2px")
  , ("box-shadow", "0 1px 2px rgba(0,0,0,0.24)")
  , ("display","flex")
  , ("justify-content","space-between")
  ]

-- for text in list item container
itemText : StyleList
itemText =
    [ ("display","inline-block")
    , ("width","10rem")
    , ("margin", "5px")
    ]

itemTextBox : StyleList
itemTextBox =
    [ ("display","inline-block")
    , ("width","2.45rem")
    , ("margin", "5px")
    ]

descriptionTextBox : StyleList
descriptionTextBox =
    [ ("width","100%")
    , ("height", "3rem")
    ]

titleTextBox : StyleList
titleTextBox = [("width","100%")]

showBtn : StyleList
showBtn = [( "display", "inline-block" )]

hideBtn : StyleList
hideBtn = [( "display", "none" )]

saveBtn : StyleList
saveBtn = [( "float", "right" )]

addBtn : StyleList
addBtn = [( "float", "right" )]

dragBtn : StyleList
dragBtn = [
    ("height","100%")
    , ("width","1rem")
    , ("background-color","#D3D3D3")
    , ("cursor","move")]

editBtn : StyleList
editBtn = [
    ("height","1.25rem")
    , ("margin", "5px")]