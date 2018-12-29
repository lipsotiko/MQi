import React from 'react';
import SockJsClient from 'react-stomp';

const Dashboard = props => {
  return (<>
    <div className='content'>Dashboard</div>
    <div className='footer'></div>
    <SockJsClient url='/ws' topics={['/topic/job']}
      onConnect={() => { console.log('CONNECTED!!!') }}
      onMessage={(msg) => { console.log(msg); }} />
  </>)
}

export default Dashboard;
