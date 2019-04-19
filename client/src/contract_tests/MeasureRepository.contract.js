import { MeasureRepository as RealMeasureRepository } from './../repositories/WebMeasureRepository'
import { MeasureRepository as FakeMeasureRepository } from './../spec/repositories/FakeMeasureRepository' 

describe('measure repository', () => {

    const repositories = [
        { type: 'fake', class: new FakeMeasureRepository() },
        { type: 'real', class: new RealMeasureRepository() }
    ]

    repositories.map(repository => {
        it(repository.type + ' repository should return a list of measures', async () => {
            const measureList = await repository.class._findAllMeasureListItems();
            expect(measureList.length).to.be.greaterThan(0);
        })
    })

})
