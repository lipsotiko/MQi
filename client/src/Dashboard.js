import React from 'react';
import SockJsClient from 'react-stomp';

const Dashboard = props => {
  return (<>
    <div className='content'>Dashboard</div>
    <div className='footer'></div>
    <SockJsClient url={process.env.REACT_APP_WS_URL} topics={['/topic/job']}
      onConnect={() => { console.log('CONNECTED!!!') }}
      onMessage={(msg) => { console.log(msg); }} />
  </>)
}

export default Dashboard;
