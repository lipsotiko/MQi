package io.egia.mqi.patient;

import io.egia.mqi.chunk.Chunk;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

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
	
	public Long getPatientId() {
		return patientId;
	}

	public void setPatientId(Long patientId) {
		this.patientId = patientId;
		chunk.setPatientId(patientId);
	}
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getMiddleName() {
		return middleName;
	}
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public Date getDateOfBirth() {
		return dateOfBirth;
	}
	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	public char getGender() {
		return gender;
	}
	public void setGender(char gender) {
		this.gender = gender;
	}
	public void setChunk(Chunk chunk) {
		this.chunk = chunk;
	}

	@Override
	public void updatePatientData(PatientData patientData) {
		patientData.addPatientRecord(this);
	}
}
