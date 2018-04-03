import * as React from 'react';
import { Component } from 'react';
import { MeasureEntity } from '../domain/Measure';
import MeasureList from './MeasureList';

interface Props {

}

interface State {
    measuresEntities: MeasureEntity[];
}

export default class Measures extends Component<Props, State> {
    constructor(props: Props) {
        super(props);
        this.state = { measuresEntities: [] };

    }

    async componentDidMount() {
        await fetch('/measure')
            .then(response => response.json())
            .then(measuresJsonArray => {
                let me: MeasureEntity[] = [];

                measuresJsonArray.map((measureJson: JSON) => {
                    me.push(MeasureEntity.fromJSON(measureJson));
                });

                this.setState({
                    measuresEntities: me
                });
            });
    }

    render() {
        const measureNames = this.state.measuresEntities.map(measureEntity => {
            return(<li key={measureEntity.measureId}>{measureEntity.measure.name}</li>);
        });

        return (
            <div>
                <h2>Measures</h2>
                <ul>
                    {measureNames}
                </ul>
                <hr />
                <MeasureList />
            </div>
        );
    }
}