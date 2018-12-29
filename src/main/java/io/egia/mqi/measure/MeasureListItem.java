package io.egia.mqi.measure;

import io.egia.mqi.job.JobStatus;

import java.util.Date;

public class MeasureListItem {
    private Long measureId;
    private String measureName;
    private JobStatus jobStatus;
    private Date jobLastUpdated;

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

    public JobStatus getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(JobStatus jobStatus) {
        this.jobStatus = jobStatus;
    }

    public Date getJobLastUpdated() {
        return jobLastUpdated;
    }

    public void setJobLastUpdated(Date jobLastUpdated) {
        this.jobLastUpdated = jobLastUpdated;
    }
}
