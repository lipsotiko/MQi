import * as React from 'react';
import { Component } from 'react';
import { MeasureEntity } from '../domain/Measure';

export default class Measures extends Component {

    private measuresEntities: MeasureEntity[] = [];

    async componentDidMount() {
        return await fetch('/measure')
            .then(response => response.json())
            .then(measuresJsonArray => measuresJsonArray.map((measureJson: JSON) => {
                this.measuresEntities.push(MeasureEntity.fromJSON(measureJson));
            }));
    }

    render() {

        const measureNames = this.measuresEntities.map((measureEntity, i) => {
            return(<li key={i}>{measureEntity.measure.name}</li>);
        });

        return (
            <div>
                <h2>Measures</h2>
                <ul>
                    {measureNames}
                </ul>
            </div>
        );
    }
}