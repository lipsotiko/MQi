import React from 'react';
import { expect } from 'chai';
import MeasureEditor from '../../MeasureEditor'
import { MeasureRepository as FakeMeasureRepository } from '../mock_repositories/FakeMeasureRepository'
import { renderIntoDocument, findRenderedDOMComponentWithClass, Simulate } from 'react-dom/test-utils';
import { forIt } from './../Helpers'
import sinon from 'sinon';

describe('Measure Editor', () => {

    let document;

    beforeEach(async () => {
        document = renderIntoDocument(<MeasureEditor currentTab={0} setTab={() => { }} measureRepository={new FakeMeasureRepository()} />);
        await forIt(100)
    });

    it('should display exisitng measures in the measure list', () => {
        const measure_editor = findRenderedDOMComponentWithClass(document, 'measure-editor');
        expect(measure_editor.querySelector('[data-testid=measure-id-1]').innerHTML).to.contain('sample measure 1');
        expect(measure_editor.querySelector('[data-testid=measure-id-2]').innerHTML).to.contain('sample measure 2');
    });

    it('should allow users to select a measure from the measure list', async () => {
        const measure_editor = findRenderedDOMComponentWithClass(document, 'measure-editor');
        const measureItemOne = measure_editor.querySelector('[data-testid=measure-id-1]');
        Simulate.click(measureItemOne);
        await forIt(100)

        const measureList = measure_editor.querySelector('[data-testid=measure-list]');
        const selectedMeausureItem = measureList.querySelector('.selected');

        expect(selectedMeausureItem.innerHTML).to.contain('sample measure 1');
        expect(measure_editor.querySelector('[data-testid=measure-name]').innerHTML).to.contain('sample measure 1');
    });

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

});

