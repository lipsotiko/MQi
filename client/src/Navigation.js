import React from 'react';

const Navigation = (props) => {
  return (
    <div className='navigation'>
      <nav onClick={() => props.navigate("Dashboard")}>Dashboard</nav>
      <nav onClick={() => props.navigate("MeasureEditor")}>Measure Editor</nav>
    </div>
  )
}

export default Navigation;
