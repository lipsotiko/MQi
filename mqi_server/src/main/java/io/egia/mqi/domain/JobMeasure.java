package io.egia.mqi.domain;

import javax.persistence.*;

@Entity
@Table(name = "t_job_measure")
public class JobMeasure {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="job_measure_id") private Long jobMeasureId;
	@Column(name="job_id") private Long jobId;
	@Column(name="measure_id") private Long measureId;
	
	public Long getJobMeasureId() {
		return jobMeasureId;
	}
	public void setJobMeasureId(Long jobMeasureId) {
		this.jobMeasureId = jobMeasureId;
	}
	public Long getJobId() {
		return jobId;
	}
	public void setJobId(Long jobId) {
		this.jobId = jobId;
	}
	public Long getMeasureId() {
		return measureId;
	}
	public void setMeasureId(Long measureId) {
		this.measureId = measureId;
	}
	
}
