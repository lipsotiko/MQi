export class MeasureEntity {
    measureId: number;
    measureName: string;
    measureLogic: MeasureLogic;
    lastUpdatedFormated: Date;
    
    static fromJSON(json: JSON): MeasureEntity {
        let measure = Object.create(MeasureEntity.prototype);
        return Object.assign(measure, json);
    }
}

class MeasureLogic {
    description: string;
}