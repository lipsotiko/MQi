import React from 'react';
import App from '../App';
import { expect } from 'chai';
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
      const { getByTestId } = render(<App />);

      const dashboardTab = await waitForElement(() => getByTestId('Dashboard'));
      fireEvent.click(dashboardTab);

      const navigation = await waitForElement(() => getByTestId('navigation'));
      const selectedTab = navigation.querySelector('.selected');
      expect(selectedTab.innerHTML).to.contain('Dashboard');
    });
  });
});
