package io.egia.mqi.patient;

import io.egia.mqi.chunk.Chunk;
import lombok.Data;

import javax.persistence.*;
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

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumns({
			@JoinColumn(name = "patientId", referencedColumnName = "patientId")
	})
	private Chunk chunk;

	@Column(updatable=false,insertable=false) private Date lastUpdated;

	@Override
	public void updatePatientData(PatientData patientData) {
		patientData.addPatientRecord(this);
	}
}
