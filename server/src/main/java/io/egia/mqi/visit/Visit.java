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
			@JoinColumn(name = "visitId")
	})
	private List<VisitCode> visitCodes;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "patientId")
	})
	private Chunk chunk;

	@Override
	public void updatePatientData(PatientData patientData) {
		patientData.addPatientRecord(this);
	}
}
