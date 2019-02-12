export const _selectMeasure = (component) => {
  let measureList = component.state.measureList;
  return (measureId, event) => {
    if (event.shiftKey || event.ctrlKey) {
      component.setState({ measureList: _selectMultipleMeasuresListItemById(measureId, measureList) });
    } else {
      component.setState({ measureList: _selectMeasureListItemById(measureId, measureList) });
    }
  }
}

export const _deleteMeasures = async (component) => {
  let selectedMeasureIds = _getSelectedMeasureIds(component.state.measureList);
  await component.props.measureRepository._deleteMeasures(selectedMeasureIds);
  let measureList = component.state.measureList.filter(m => !selectedMeasureIds.includes(m.measureId));
  component.setState({ measureList, measure: null });
}

export const _processMeasures = async (component) => {
  let selectedMeasureIds = _getSelectedMeasureIds(component.state.measureList);
  component.props.measureRepository._processMeasures(selectedMeasureIds);
  let measureList = component.state.measureList.map(m => {
    if (selectedMeasureIds.includes(m.measureId)) {
      m.jobStatus = "RUNNING";
      m.selected = false;
      return m;
    };
    return m;
  });
  component.setState({ measureList, measure: null });
}

const _selectMeasureListItemById = (measureId, measureList) => {
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

const _selectMultipleMeasuresListItemById = (measureId, measureList) => {
  measureList.map(measureListItem => {
    if (measureListItem.measureId === measureId) {
      measureListItem.selected = true;
      return measureListItem;
    }
    return measureListItem;
  })
  return measureList;
}

const _getSelectedMeasureIds = (measureList) => {
  let selectedMeasureIds = [];
  measureList.forEach(measureListItem => {
    if (measureListItem.selected) {
      selectedMeasureIds.push(measureListItem.measureId);
    }
  });
  return selectedMeasureIds;
}
