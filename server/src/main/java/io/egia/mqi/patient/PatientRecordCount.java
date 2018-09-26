package io.egia.mqi.patient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@AllArgsConstructor
public class PatientRecordCount {
    @Id Long patientId;
    LocalDateTime lastUpdated;
    Long recordCount;

    public PatientRecordCount () {

    }
}
