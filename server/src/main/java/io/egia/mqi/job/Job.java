package io.egia.mqi.job;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
public class Job {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long jobId;
	private String jobName;
	@Column(nullable=false) private String processType;
	private Integer orderId;
	private String status;
	private Date startTime;
	private Date endTime;
	@Column(updatable=false,insertable=false) private Date lastUpdated;
}
