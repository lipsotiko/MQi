import React from 'react';
import App from '../App';
import { renderIntoDocument, findRenderedDOMComponentWithClass, Simulate } from 'react-dom/test-utils';
import { expect } from 'chai';
import { MeasureRepository as FakeMeasureRepository } from './mock_repositories/FakeMeasureRepository'
import { forIt } from './Helpers'

describe('MQi GUI', () => {

  let document;

  beforeEach(async () => {
    document = renderIntoDocument(<App measureRepository={new FakeMeasureRepository()} resultsRepository={null} />);
    await forIt(100);
  });

  it('should render without crashing', () => {
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
