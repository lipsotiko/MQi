package io.egia.mqi.integration;

import io.egia.mqi.measure.Measure;
import io.egia.mqi.measure.PublicMeasureListItem;
import io.egia.mqi.measure.PublicMeasureRepo;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

abstract class PublicMeasureRepoTest {

    private PublicMeasureRepo<Measure> publicMeasureRepo;
    private String objectName = "2ec8c7b1-81da-4e44-8fc5-2cb0a4f0bf94_BIKE.json";
    abstract void setMeasureBucketUrl(PublicMeasureRepo publicMeasureRepo);
    abstract PublicMeasureRepo<Measure> getPublicMeasureRepo();

    @Before
    public void setUp() {
        publicMeasureRepo = getPublicMeasureRepo();
        setMeasureBucketUrl(publicMeasureRepo);
    }

    @Test
    public void returns_a_list_of_public_measures() {
        List<PublicMeasureListItem> measureList = publicMeasureRepo.getMeasureList();
        PublicMeasureListItem bikeMeasureItem = measureList.stream()
                .filter(pm -> pm.getObjectName().equals(objectName))
                .collect(Collectors.toList()).get(0);
        assertThat(bikeMeasureItem.getMeasureName()).isEqualTo("BIKE.json");
        assertThat(bikeMeasureItem.getInstalled()).isNull();
    }

    @Test
    public void returns_a_measure() throws IOException {
        Measure measure = publicMeasureRepo.getMeasure(objectName);
        assertThat(measure.getMeasureId()).isEqualTo(UUID.fromString("2ec8c7b1-81da-4e44-8fc5-2cb0a4f0bf94"));
    }

}
