package io.egia.mqi.job;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({JobListener.class})
public class Job {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	private JobStatus jobStatus;
	private Date startTime;
	private Date endTime;
	private Long initialPatientCount;
	private Long processedPatientCount;

	@ElementCollection(fetch = FetchType.EAGER)
	private List<Long> measureIds;

	@Column(updatable=false,insertable=false) private Date lastUpdated;

	@JsonProperty("progress")
	int getProgress() {
		if (jobStatus == null || initialPatientCount == null || processedPatientCount == null)
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
