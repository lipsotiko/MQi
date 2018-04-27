package io.egia.mqi.visit;

import io.egia.mqi.chunk.Chunk;
import io.egia.mqi.patient.PatientData;
import io.egia.mqi.patient.PatientRecordInterface;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
public class Visit implements PatientRecordInterface, Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long visitId;
	@Column(name="patient_id") private Long patientId;
	private Date dateOfService;
	private Date admitDt;
	private Date dischargeDt;
	private Integer coveredDays;
	private String cpt;
	private String cpt2;
	private String cptMod1;
	private String cptMod2;
	private String hcpcs;
	private String diag2;
	private String diag1;
	private String diag3;
	private String diag4;
	private String diag5;
	private String diag6;
	private String diag7;
	private String diag8;
	private String diag9;
	private String diag10;
	private String proc1;
	private String proc2;
	private String proc3;
	private String proc4;
	private String proc5;
	private String proc6;
	private String proc7;
	private String proc8;
	private String proc9;
	private String proc10;
	private Integer icdVersion;
	private String drg;
	private Integer drgVersion;
	private Integer dischargeStatus;
	private String ubRev;
	private String typeOfBill;
	private String units;
	private String placeOfService;
	private Boolean denied;
	private String providerId;
	private Boolean supplemental;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="patient_id",updatable=false,insertable=false)
	private Chunk chunk;

	@Override
	public void updatePatientData(PatientData patientData) {
		patientData.addPatientRecord(this);
	}
}
