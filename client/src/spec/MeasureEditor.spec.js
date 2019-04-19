// import React from 'react';
// import { expect } from 'chai';
// import MeasureEditor from '../MeasureEditor.js'
// import { MeasureRepository } from './repositories/FakeMeasureRepository.js'
// import sinon from 'sinon';

// xdescribe('Measure Editor', () => { 

//   it('should display exisitng measures in the measure list', async () => {
//     const { getByTestId } = render(<MeasureEditor measureRepository={new MeasureRepository()} />);

//     const measureOne = await waitForElement(() => getByTestId('measure-id-1'));
//     expect(measureOne.innerHTML).to.contain('sample measure 1');

//     const measureTwo = await waitForElement(() => getByTestId('measure-id-2'));
//     expect(measureTwo.innerHTML).to.contain('sample measure 2');
//   });

//   it('should allow users to select a measure from the measure list', async () => {
//     const { getByTestId } = render(<MeasureEditor measureRepository={new MeasureRepository()} />);
    
//     const measureOne = await waitForElement(() => getByTestId('measure-id-1'));
//     await fireEvent.click(measureOne);

//     const measureList = getByTestId('measure-list');
//     const selectedMeasureListItem = measureList.querySelector('.selected');
//     expect(selectedMeasureListItem.innerHTML).to.contain('sample measure 1');
//     expect(getByTestId('measure').innerHTML).to.contain('sample measure 1');
//   });

//   it('should allow users to create a new measure', async () => {
//     let saveMeasureSpy = sinon.spy();
//     const { getByText } = render(<MeasureEditor measureRepository={new MeasureRepository(saveMeasureSpy)} />);
    
//     const addMeasureBtn = await waitForElement(() => getByText('+ Measure'));
//     await fireEvent.click(addMeasureBtn);


//     const saveMeasureBtn = await waitForElement(() => getByText('Save'));
//     await fireEvent.click(saveMeasureBtn)

//     const savedMeasure = saveMeasureSpy.getCall(0).args[0];
//     expect(savedMeasure.measureId).to.eq(-1);
//     expect(savedMeasure.measureName).to.eq('New Measure');
//     expect(savedMeasure.measureLogic.description).to.eq('Describe...');
//   });

//   it('should allow users to process a measure', async () => {

//   });

//   it('should allow users to delete a measure', async () => {

//   });

// });

