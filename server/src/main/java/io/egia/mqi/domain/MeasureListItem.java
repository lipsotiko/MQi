package io.egia.mqi.domain;

public class MeasureListItem {
    private Long measureId;
    private String fileName;

    public MeasureListItem(Long measureId, String fileName) {
        this.measureId = measureId;
        this.fileName = fileName;
    }

    public Long getMeasureId() {
        return measureId;
    }

    public void setMeasureId(Long measureId) {
        this.measureId = measureId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
