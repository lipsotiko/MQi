import React from 'react';
import SockJsClient from 'react-stomp';

const Dashboard = props => {
  return (<>
      <div>Dashboard</div>
      <SockJsClient url='http://localhost:8080/ws' topics={['/topic/job']}
        onConnect={() => { console.log('CONNECTED!!!') }}
        onMessage={(msg) => { console.log(msg); }} />
    </>)
}

export default Dashboard;
