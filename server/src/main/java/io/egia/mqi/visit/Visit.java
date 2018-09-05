package io.egia.mqi.visit;

import io.egia.mqi.chunk.Chunk;
import io.egia.mqi.patient.PatientData;
import io.egia.mqi.patient.PatientRecordInterface;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@Entity
public class Visit implements PatientRecordInterface {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long visitId;

	@Column(insertable= false, updatable=false)
	private Long patientId;
	private Date dateOfService;
	private Date admitDt;
	private Date dischargeDt;
	private Integer dischargeStatus;
	private Integer coveredDays;
	private String units;
	private Boolean denied;
	private String providerId;
	private Boolean supplemental;

	private String primaryDxCode;
	private Integer primaryDxCodeVersion;

	@OneToMany(fetch = FetchType.EAGER)
	@JoinColumns({
			@JoinColumn(name = "visitId", referencedColumnName = "visitId")
	})
	private List<VisitCode> visitCodes;

	private String drg;
	private Integer drgVersion;

//	private String proc1;
//	private String proc2;
//	private String proc3;
//	private String proc4;
//	private String proc5;
//	private String proc6;
//	private String proc7;
//	private String proc8;
//	private String proc9;
//	private String proc10;
//	private String ubRev;
//	private String typeOfBill;
//	private String placeOfService;
// 	private String cpt;
//	private String cpt2;
//	private String cptMod1;
//	private String cptMod2;
//	private String hcpcs;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumns({
			@JoinColumn(name = "patientId", referencedColumnName = "patientId")
	})
	private Chunk chunk;

	@Override
	public void updatePatientData(PatientData patientData) {
		patientData.addPatientRecord(this);
	}
}
