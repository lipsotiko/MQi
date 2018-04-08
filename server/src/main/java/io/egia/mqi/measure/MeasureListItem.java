package io.egia.mqi.measure;

public class MeasureListItem {
    private Long measureId;
    private String measureName;

    public MeasureListItem(Long measureId, String measureName) {
        this.measureId = measureId;
        this.measureName = measureName;
    }

    public Long getMeasureId() {
        return measureId;
    }

    public void setMeasureId(Long measureId) {
        this.measureId = measureId;
    }

    public String getMeasureName() {
        return measureName;
    }

    public void setMeasureName(String measureName) {
        this.measureName = measureName;
    }
}
