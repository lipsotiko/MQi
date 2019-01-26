export const selectMeasureListItemById = (measureId, measureList) => {
  measureList.map(measureListItem => measureListItem.selected = false)
  measureList.map(measureListItem => {
    if (measureListItem.measureId === measureId) {
      measureListItem.selected = true;
      return measureListItem;
    }
    return measureListItem;
  })
  return measureList;
}

export const selectMultipleMeasuresListItemById = (measureId, measureList) => {
  measureList.map(measureListItem => {
    if (measureListItem.measureId === measureId) {
      measureListItem.selected = true;
      return measureListItem;
    }
    return measureListItem;
  })
  return measureList;
}
