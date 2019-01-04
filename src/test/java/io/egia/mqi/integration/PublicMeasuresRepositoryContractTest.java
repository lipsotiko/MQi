package io.egia.mqi.integration;

import io.egia.mqi.measure.PublicMeasuresRepository;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.test.util.ReflectionTestUtils;

public class PublicMeasuresRepositoryContractTest {

    @Test
    public void returns_a_list_of_public_measures() {
        String measureBucketUrl = "https://www.googleapis.com/storage/v1/b/mqi-measures/o";
        PublicMeasuresRepository publicMeasuresRepository = new PublicMeasuresRepository();
        ReflectionTestUtils.setField(publicMeasuresRepository, "measureBucketUrl", measureBucketUrl);
        assertThat(publicMeasuresRepository.getMeasureList().get(0)).isEqualTo("BIKE.json");
    }
}
