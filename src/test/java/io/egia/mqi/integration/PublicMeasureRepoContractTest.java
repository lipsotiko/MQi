package io.egia.mqi.integration;

import io.egia.mqi.measure.Measure;
import io.egia.mqi.measure.PublicMeasuresRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class PublicMeasuresRepositoryContractTest {

    private String measureBucketUrl = "https://www.googleapis.com/storage/v1/b/mqi-measures/o";

    private PublicMeasuresRepository publicMeasuresRepository;

    @Before
    public void setUp() {
        publicMeasuresRepository = new PublicMeasuresRepository();
        ReflectionTestUtils.setField(publicMeasuresRepository, "measureBucketUrl", measureBucketUrl);
    }

    @Test
    public void returns_a_list_of_public_measures() {
        List<String> measureList = publicMeasuresRepository.getMeasureList();
        assertThat(measureList.get(0)).isEqualTo("BIKE.json");
    }

    @Test
    public void returns_a_measure() throws IOException {
        Measure measure = publicMeasuresRepository.getMeasure("BIKE.json");
        assertThat(measure.getMeasureId()).isEqualTo(UUID.fromString("2ec8c7b1-81da-4e44-8fc5-2cb0a4f0bf94"));
    }
}
