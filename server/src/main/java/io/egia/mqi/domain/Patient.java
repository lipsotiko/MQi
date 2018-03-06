package io.egia.mqi.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name = "t_patient")
public class Patient implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="patient_id") private Long patientId;
	@Column(name="first_name") private String firstName;
	@Column(name="middle_name") private String middleName;
	@Column(name="last_name") private String lastName;
	@Column(name="date_of_birth") private Date dateOfBirth;
	@Column(name="gender") private char gender;
	@Column(name="last_updated",updatable=false,insertable=false) private Date lastUpdated;
	
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
	
}
