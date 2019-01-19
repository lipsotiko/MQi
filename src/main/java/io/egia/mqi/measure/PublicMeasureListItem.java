package io.egia.mqi.measure;

import lombok.Data;

import java.util.UUID;

@Data
public class PublicMeasureListItem {
    private String objectName;
    private UUID measureId;
    private String measureName;
    private Boolean installed;

    public PublicMeasureListItem(UUID measureId, String measureName, Boolean installed) {
        this.measureId = measureId;
        this.measureName = measureName;
        this.installed = installed;
        this.objectName = measureId + "_" + measureName;
    }

    public PublicMeasureListItem(String objectName) {
        this.objectName = objectName;
        this.measureId = this.uuidFromObjectName(objectName);
        this.measureName = objectName.substring(objectName.indexOf("_") + 1);
    }

    public PublicMeasureListItem() {

    }

    public UUID uuidFromObjectName(String objectName) {
        return UUID.fromString(objectName.substring(0, objectName.indexOf("_")));
    }

}
