export class MeasureEntity {
    measureId: Number;
    fileName: String;
    measure: Measure;
    lastUpdatedFormated: Date;
    
    static fromJSON(json: JSON): MeasureEntity {
        let measure = Object.create(MeasureEntity.prototype);
        return Object.assign(measure, json);
    }
}

class Measure {
    name: string;
}