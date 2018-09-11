package io.egia.mqi.patient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PatientMeasureLog {

    @Id
    Long patientId;
    Long measureId;
    @Column(updatable=false,insertable=false) private Date lastUpdated;
}
