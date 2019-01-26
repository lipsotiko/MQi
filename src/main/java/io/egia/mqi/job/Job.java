package io.egia.mqi.job;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({JobListener.class})
public class Job {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    private JobStatus jobStatus;
    private Date startTime;
    private Date endTime;
    private Long initialPatientCount;
    private Long processedPatientCount;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<UUID> measureIds;

    private ZonedDateTime lastUpdated;

    @JsonProperty("progress")
    int getProgress() {
        if (jobStatus == null || initialPatientCount == null || processedPatientCount == null)
            return 0;
        switch (jobStatus) {
            case RUNNING:
                return (int) ((processedPatientCount * 100.0f) / initialPatientCount);
            case DONE:
                return 100;
            default:
                return 0;
        }
    }
}
