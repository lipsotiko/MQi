import React from 'react';
import { expect } from 'chai';
import MeasureEditor from '.././MeasureEditor.js'
import { FakeMeasureRepository } from './repositories/FakeMeasureRepository.js'
import { render, cleanup, waitForElement, fireEvent } from 'react-testing-library'

describe('Measure Editor', () => {

  afterEach(() => {
    cleanup()
  });

  it('should display exisitng measures in the measure list', async () => {
    const { getByTestId } = render(<MeasureEditor measureRepository={new FakeMeasureRepository()} />);

    const measureOne = await waitForElement(() => getByTestId('measure-id-1'));
    expect(measureOne.innerHTML).to.contain('sample measure 1');

    const measureTwo = await waitForElement(() => getByTestId('measure-id-2'));
    expect(measureTwo.innerHTML).to.contain('sample measure 2');
  });

  it('should display exisitng measures in the measure list 2', async () => {
    const { getByTestId } = render(<MeasureEditor measureRepository={new FakeMeasureRepository()} />);
    const measureOne = await waitForElement(() => getByTestId('measure-id-1'));
    fireEvent.click(measureOne);

    const measureList = getByTestId('measure-list');
    const selectedMeasure = measureList.querySelector('.selected');
    expect(selectedMeasure.innerHTML).to.contain('sample measure 1');
  });
})
