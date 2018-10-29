package io.egia.mqi.job;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({JobListener.class})
public class Job {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long jobId;
	private JobStatus jobStatus;
	private Date startTime;
	private Date endTime;
	private Long initialPatientCount;
	private Long processedPatientCount;

	@Column(updatable=false,insertable=false) private Date lastUpdated;

	@JsonProperty("progress")
	int getProgress() {
		if (jobStatus == null)
			return 0;
		switch (jobStatus) {
			case RUNNING :
				return (int)((processedPatientCount * 100.0f) / initialPatientCount);
			case DONE:
				return 100;
			default:
				return 0;
		}
	}
}
