package io.egia.mqi.integration;

import io.egia.mqi.measure.Measure;
import io.egia.mqi.measure.PublicMeasureListItem;
import io.egia.mqi.measure.PublicMeasureRepo;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static io.egia.mqi.helpers.Helpers.getMeasureFromResource;

public class FakePublicMeasureRepo implements PublicMeasureRepo<Measure> {

    private UUID bikeUUID = UUID.fromString("2ec8c7b1-81da-4e44-8fc5-2cb0a4f0bf94");

    @Override
    public List<PublicMeasureListItem> getMeasureList() {
        PublicMeasureListItem bike = new PublicMeasureListItem(bikeUUID, "BIKE.json", null);
        return Collections.singletonList(bike);
    }

    @Override
    public Measure getMeasure(String publicMeasureObjectName) throws IOException {
        Measure measure = getMeasureFromResource("fixtures", "BIKE.json");
        measure.setMeasureId(bikeUUID);
        return measure;
    }
}
