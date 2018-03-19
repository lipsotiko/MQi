import * as React from 'react';
import { Component } from 'react';

interface State {
    measures: JSON;
}

interface Props {
}

export default class Measures extends Component<Props, State> {

    public state: State;

    constructor(props: {}) {
        super(props);
        this.state = {
            measures: JSON
        };
    }

    componentDidMount() {
        return fetch('http://localhost:8080/measure')
            .then(response => response.json())
            .then(measures => this.setState({measures}));
    }

    render() {
        return (
            <div>
                <h2>Measures</h2>
                <p>{JSON.stringify(this.state.measures)}</p>
                {/*{this.state.measures.map((m, i) =>*/}
                    {/*<p key={i}>{i}. {m}</p>*/}
                {/*)}*/}
            </div>
        );
    }
}