export class MeasureEntity {
    measureId: number;
    fileName: String;
    measure: Measure;
    lastUpdatedFormated: String;

    static fromJSON(json: JSON): MeasureEntity {
        let measure = Object.create(MeasureEntity.prototype);
        return Object.assign(measure, json);
    }
}

class Measure {
    name: string;
}