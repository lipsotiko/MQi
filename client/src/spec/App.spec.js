import React from 'react';
import App from '../App';
import { renderIntoDocument, findRenderedDOMComponentWithClass, Simulate } from 'react-dom/test-utils';
import { expect } from 'chai';
import { MeasureRepository } from './repositories/FakeMeasureRepository.js'
import { ResultsRepository } from './repositories/FakeResultsRepository.js'


describe('MQi GUI', () => {

  let component;
  let document;

  beforeEach(() => {
    component = <App measureRepository = { new MeasureRepository() } resultsRepository = {new ResultsRepository()}/>;
    document = renderIntoDocument(component);
  });

  it('should render without crashing', () => {

    console.log(process.env)
    expect(document).to.exist;
  });

  describe('Navigation', () => {
    it('should navigate to the Editor', () => {
      let editorTab = getElementByClass('editor-tab')
      expect(editorTab.getAttribute('aria-selected')).to.eq('false');

      Simulate.click(editorTab);

      editorTab = getElementByClass('editor-tab')
      expect(editorTab.getAttribute('aria-selected')).to.eq('true');
    });
  });

  function getElementByClass(className) {
    return findRenderedDOMComponentWithClass(document, className);
  }
});
