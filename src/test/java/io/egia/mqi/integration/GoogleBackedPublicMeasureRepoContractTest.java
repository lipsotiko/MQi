package io.egia.mqi.integration;

import io.egia.mqi.measure.GoogleBackedPublicMeasureRepo;
import io.egia.mqi.measure.Measure;
import io.egia.mqi.measure.PublicMeasureRepo;
import org.springframework.test.util.ReflectionTestUtils;

public class GoogleBackedPublicMeasureRepoContractTest extends PublicMeasureRepoTest {

    @Override
    void setMeasureBucketUrl(PublicMeasureRepo publicMeasureRepo) {
        ReflectionTestUtils.setField(publicMeasureRepo, "measureBucketUrl", "https://www.googleapis.com/storage/v1/b/mqi-measures/o");
    }

    @Override
    PublicMeasureRepo<Measure> getPublicMeasureRepo() {
        return new GoogleBackedPublicMeasureRepo();
    }

}
