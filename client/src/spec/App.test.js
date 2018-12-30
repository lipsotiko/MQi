import React from 'react';
import App from '../App';
import { expect } from 'chai';
import { forIt } from './Helpers'
import { render, cleanup, waitForElement, fireEvent } from 'react-testing-library'

describe('MQi GUI', () => {

  afterEach(() => {
    cleanup()
  });

  it('should render without crashing', () => {
    render(<App />);
  });

  describe('Navigation', () => {
    it('should navigate to the Dashboard', async () => {
      const { getByTestId, container } = render(<App />);

      const dashboardTab = await waitForElement(() => getByTestId('dashboard'));
      fireEvent.click(dashboardTab);
      await forIt();

      expect(container.innerHTML).to.contain('Dashboard');
    });
  });
});
