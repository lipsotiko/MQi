import React from 'react';
import { expect } from 'chai';
import MeasureEditor from '../../MeasureEditor'
import { MeasureRepository as FakeMeasureRepository } from '../mock_repositories/FakeMeasureRepository'
import { renderIntoDocument, findRenderedDOMComponentWithClass, Simulate } from 'react-dom/test-utils';
import { forIt } from './../Helpers'
import sinon from 'sinon';

describe('Measure Editor', () => {

    let document;
    let saveMeasureSpy;

    beforeEach(async () => {
        saveMeasureSpy = sinon.spy();
        document = renderIntoDocument(
            <MeasureEditor
                currentTab={1}
                setTab={() => { }}
                measureRepository={new FakeMeasureRepository(saveMeasureSpy)} />
        );
        await forIt(1000);
    });

    it('should display exisitng measures in the measure list', () => {
        let measureEditor = findRenderedDOMComponentWithClass(document, 'measure-editor');
        expect(measureEditor.querySelector('[data-testid=measure-id-1]').innerHTML).to.contain('sample measure 1');
        expect(measureEditor.querySelector('[data-testid=measure-id-2]').innerHTML).to.contain('sample measure 2');
    });

    it('should allow users to select a measure from the measure list', async () => {
        let measureEditor = findRenderedDOMComponentWithClass(document, 'measure-editor');
        const measureItemOne = measureEditor.querySelector('[data-testid=measure-id-1]');
        Simulate.click(measureItemOne);
        await forIt(100);

        const measureList = measureEditor.querySelector('[data-testid=measure-list]');
        const selectedMeausureItem = measureList.querySelector('.selected');

        expect(selectedMeausureItem.innerHTML).to.contain('sample measure 1');
        expect(measureEditor.querySelector('[data-testid=measure-name]').innerHTML).to.contain('sample measure 1');
    });

    it('should allow users to create a new measure', async () => {
        let rightNavButtons = findRenderedDOMComponentWithClass(document, 'right-nav-buttons');
        const addMeasureButton = rightNavButtons.querySelector('.add-measure');
        
        Simulate.click(addMeasureButton);
        await forIt(100);
      
        const savedMeasure = saveMeasureSpy.getCall(0).args[0];
        expect(savedMeasure.measureName).to.eq('New Measure');
        expect(savedMeasure.measureLogic.description).to.eq('Describe...');
    });

    //   it('should allow users to process a measure', async () => {

    //   });

    //   it('should allow users to delete a measure', async () => {

    //   });

});

