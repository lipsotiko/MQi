import * as React from 'react';
import { Component } from 'react';
import { MeasureListItem } from '../domain/MeasureListItem';

interface Props {

}

interface State {
    measureListItems: MeasureListItem[];
}

export default class MeasureList extends Component<Props, State> {
    constructor(props: Props) {
        super(props);
        this.state = { measureListItems: [] };

    }

    async componentDidMount() {
        await fetch('/measure_list')
            .then(response => response.json())
            .then(measuresJsonArray => {
                let mli: MeasureListItem[] = [];

                measuresJsonArray.map((measureJson: JSON) => {
                    mli.push(MeasureListItem.fromJSON(measureJson));
                });

                this.setState({
                    measureListItems: mli
                });
            });
    }

    render() {
        const measureList = this.state.measureListItems.map(mli => {
            return(<li key={mli.measureId}>{mli.measureName.replace('.json', '')}</li>);
        });

        return (
            <div>
                <ul>
                    {measureList}
                </ul>
            </div>
        );
    }
}