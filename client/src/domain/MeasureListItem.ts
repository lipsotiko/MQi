export class MeasureListItem {
    measureId: number;
    fileName: string;

    static fromJSON(json: JSON): MeasureListItem {
        let measure = Object.create(MeasureListItem.prototype);
        return Object.assign(measure, json);
    }
}