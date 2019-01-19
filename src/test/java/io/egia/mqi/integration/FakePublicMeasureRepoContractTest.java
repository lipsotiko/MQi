package io.egia.mqi.integration;

import io.egia.mqi.measure.Measure;
import io.egia.mqi.measure.PublicMeasureRepo;

public class FakePublicMeasureRepoContractTest extends PublicMeasureRepoTest {

    @Override
    void setMeasureBucketUrl(PublicMeasureRepo publicMeasureRepo) {

    }

    @Override
    PublicMeasureRepo<Measure> getPublicMeasureRepo() {
        return new FakePublicMeasureRepo();
    }

}
