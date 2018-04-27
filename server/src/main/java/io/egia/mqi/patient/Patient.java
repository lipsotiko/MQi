package io.egia.mqi.patient;

import io.egia.mqi.chunk.Chunk;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
public class Patient implements PatientRecordInterface, Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long patientId;
	private String firstName;
	private String middleName;
	private String lastName;
	private Date dateOfBirth;
	private char gender;
	@Column(updatable=false,insertable=false) private Date lastUpdated;
	
	@MapsId
	@OneToOne(optional = false, fetch = FetchType.LAZY)
	private Chunk chunk;

	public void setPatientId(Long patientId) {
		this.patientId = patientId;
		chunk.setPatientId(patientId);
	}

	@Override
	public void updatePatientData(PatientData patientData) {
		patientData.addPatientRecord(this);
	}
}
