package io.egia.mqi.measure;

public class MeasureCount {
    private Long measureId;
    private Long count;

    public MeasureCount(Long measureId, Long count) {
        this.measureId = measureId;
        this.count = count;
    }

    public Long getMeasureId() {
        return measureId;
    }

    public void setMeasureId(Long measureId) {
        this.measureId = measureId;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
