import { MeasureRepository as RealMeasureRepository } from '../../repositories/MeasureRepository'
import { MeasureRepository as FakeMeasureRepository } from './../mock_repositories/FakeMeasureRepository'

describe('repositories', () => {

    describe('measure', () => {

        let measureRepositories = [
            { type: 'fake', class: new FakeMeasureRepository() },
            { type: 'real', class: new RealMeasureRepository() }
        ]
    
        measureRepositories.map(repository => {
            it(repository.type + ' repository should return a list of measures', async () => {
                const measureList = await repository.class._findAllMeasureListItems();
                expect(measureList.length).to.be.greaterThan(0);
                expect(measureList[0]).to.contain.keys('measureId', 'measureName', 'jobStatus', 'measureLastUpdated', 'jobLastUpdated');
            });
        });
    });
});
