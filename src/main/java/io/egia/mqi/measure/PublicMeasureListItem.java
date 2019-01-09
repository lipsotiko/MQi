package io.egia.mqi.measure;

import io.egia.mqi.job.JobStatus;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
public class PublicMeasureListItem {
    private UUID measureId;
    private String measureName;
    private ZonedDateTime measureLastUpdated;
    private JobStatus jobStatus;
    private ZonedDateTime jobLastUpdated;

    public PublicMeasureListItem(UUID measureId, String measureName, ZonedDateTime measureLastUpdated) {
        this.measureId = measureId;
        this.measureName = measureName;
        this.measureLastUpdated = measureLastUpdated;
    }

}
