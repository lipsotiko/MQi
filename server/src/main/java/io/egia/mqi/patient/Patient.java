package io.egia.mqi.patient;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Entity
public class Patient implements PatientRecordInterface {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long patientId;
	private String firstName;
	private String middleName;
	private String lastName;
	private Date dateOfBirth;
	private char gender;

	@Column(updatable=false,insertable=false) private LocalDateTime lastUpdated;

	@Override
	public void updatePatientData(PatientData patientData) {
		patientData.addPatientRecord(this);
	}
}
