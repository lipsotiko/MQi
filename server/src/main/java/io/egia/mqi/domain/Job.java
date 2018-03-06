package io.egia.mqi.domain;

import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name = "t_job")
public class Job {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="job_id") private Long jobId;
	@Column(name="job_name") private String jobName;
	@Column(name="process_type", nullable=false) private String processType;
	@Column(name="order_id") private Integer orderId;
	@Column(name="status") private String status;
	@Column(name="start_time") private Date startTime;
	@Column(name="end_time") private Date endTime;
	@Column(name="last_updated",updatable=false,insertable=false) private Date lastUpdated;
	
	public Long getJobId() {
		return jobId;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getProcessType() {
		return processType;
	}

	public void setProcessType(String processType) {
		this.processType = processType;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}	
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}
}
