package io.egia.mqi.patient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PatientMeasureLog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    Long patientId;
    UUID measureId;
    @Column(updatable = false, insertable = false)
    private ZonedDateTime lastUpdated;
}
