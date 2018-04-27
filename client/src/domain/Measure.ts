export class MeasureEntity {
    measureId: number;
    measureName: string;
    measureJson: MeasureJson;
    lastUpdatedFormated: Date;
    
    static fromJSON(json: JSON): MeasureEntity {
        let measure = Object.create(MeasureEntity.prototype);
        return Object.assign(measure, json);
    }
}

class MeasureJson {
    description: string;
}