package io.egia.mqi.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name = "t_visit")
public class Visit implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="visit_id") private Long visitId;
	@Column(name="patient_id") private Long patientId;
	@Column(name="date_of_service") private Date dateOfService;
	@Column(name="admit_dt") private Date admitDt;
	@Column(name="discharge_dt") private Date dischargeDt;
	@Column(name="covered_days") private Integer coveredDays;
	@Column(name="cpt") private String cpt;
	@Column(name="cpt2") private String cpt2;
	@Column(name="cpt_mod1") private String cptMod1;
	@Column(name="cpt_mod2") private String cptMod2;
	@Column(name="hcpcs") private String hcpcs;
	@Column(name="diag1") private String diag1;
	@Column(name="diag2") private String diag2;
	@Column(name="diag3") private String diag3;
	@Column(name="diag4") private String diag4;
	@Column(name="diag5") private String diag5;
	@Column(name="diag6") private String diag6;
	@Column(name="diag7") private String diag7;
	@Column(name="diag8") private String diag8;
	@Column(name="diag9") private String diag9;
	@Column(name="diag10") private String diag10;
	@Column(name="proc1") private String proc1;
	@Column(name="proc2") private String proc2;
	@Column(name="proc3") private String proc3;
	@Column(name="proc4") private String proc4;
	@Column(name="proc5") private String proc5;
	@Column(name="proc6") private String proc6;
	@Column(name="proc7") private String proc7;
	@Column(name="proc8") private String proc8;
	@Column(name="proc9") private String proc9;
	@Column(name="proc10") private String proc10;
	@Column(name="icd_version") private Integer icdVersion;
	@Column(name="drg") private String drg;
	@Column(name="drg_version") private Integer drgVersion;
	@Column(name="discharge_status") private Integer dischargeStatus;
	@Column(name="ubrev") private String ubRev;
	@Column(name="type_of_bill") private String typeOfBill;
	@Column(name="units") private String units;
	@Column(name="place_of_service") private String placeOfService;
	@Column(name="denied") private Boolean denied;
	@Column(name="provider_id") private String providerId;
	@Column(name="supplemental") private Boolean supplemental;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="patient_id",updatable=false,insertable=false)
	private Chunk chunk;
	
	public Long getVisitId() {
		return visitId;
	}
	public void setVisitId(Long visitId) {
		this.visitId = visitId;
	}
	public Long getPatientId() {
		return patientId;
	}
	public void setPatientId(Long patientId) {
		this.patientId = patientId;
	}
	public Date getDateOfService() {
		return dateOfService;
	}
	public void setDateOfService(Date dateOfService) {
		this.dateOfService = dateOfService;
	}
	public Date getAdmitDt() {
		return admitDt;
	}
	public void setAdmitDt(Date admitDt) {
		this.admitDt = admitDt;
	}
	public Date getDischargeDt() {
		return dischargeDt;
	}
	public void setDischargeDt(Date dischargeDt) {
		this.dischargeDt = dischargeDt;
	}
	public int getCoveredDays() {
		return coveredDays;
	}
	public void setCoveredDays(int coveredDays) {
		this.coveredDays = coveredDays;
	}
	public String getCpt() {
		return cpt;
	}
	public void setCpt(String cpt) {
		this.cpt = cpt;
	}
	public String getCpt2() {
		return cpt2;
	}
	public void setCpt2(String cpt2) {
		this.cpt2 = cpt2;
	}
	public String getCptMod1() {
		return cptMod1;
	}
	public void setCptMod1(String cptMod1) {
		this.cptMod1 = cptMod1;
	}
	public String getCptMod2() {
		return cptMod2;
	}
	public void setCptMod2(String cptMod2) {
		this.cptMod2 = cptMod2;
	}
	public String getHcpcs() {
		return hcpcs;
	}
	public void setHcpcs(String hcpcs) {
		this.hcpcs = hcpcs;
	}
	public String getDiag1() {
		return diag1;
	}
	public void setDiag1(String diag1) {
		this.diag1 = diag1;
	}
	public String getDiag2() {
		return diag2;
	}
	public void setDiag2(String diag2) {
		this.diag2 = diag2;
	}
	public String getDiag3() {
		return diag3;
	}
	public void setDiag3(String diag3) {
		this.diag3 = diag3;
	}
	public String getDiag4() {
		return diag4;
	}
	public void setDiag4(String diag4) {
		this.diag4 = diag4;
	}
	public String getDiag5() {
		return diag5;
	}
	public void setDiag5(String diag5) {
		this.diag5 = diag5;
	}
	public String getDiag6() {
		return diag6;
	}
	public void setDiag6(String diag6) {
		this.diag6 = diag6;
	}
	public String getDiag7() {
		return diag7;
	}
	public void setDiag7(String diag7) {
		this.diag7 = diag7;
	}
	public String getDiag8() {
		return diag8;
	}
	public void setDiag8(String diag8) {
		this.diag8 = diag8;
	}
	public String getDiag9() {
		return diag9;
	}
	public void setDiag9(String diag9) {
		this.diag9 = diag9;
	}
	public String getDiag10() {
		return diag10;
	}
	public void setDiag10(String diag10) {
		this.diag10 = diag10;
	}
	public String getProc1() {
		return proc1;
	}
	public void setProc1(String proc1) {
		this.proc1 = proc1;
	}
	public String getProc2() {
		return proc2;
	}
	public void setProc2(String proc2) {
		this.proc2 = proc2;
	}
	public String getProc3() {
		return proc3;
	}
	public void setProc3(String proc3) {
		this.proc3 = proc3;
	}
	public String getProc4() {
		return proc4;
	}
	public void setProc4(String proc4) {
		this.proc4 = proc4;
	}
	public String getProc5() {
		return proc5;
	}
	public void setProc5(String proc5) {
		this.proc5 = proc5;
	}
	public String getProc6() {
		return proc6;
	}
	public void setProc6(String proc6) {
		this.proc6 = proc6;
	}
	public String getProc7() {
		return proc7;
	}
	public void setProc7(String proc7) {
		this.proc7 = proc7;
	}
	public String getProc8() {
		return proc8;
	}
	public void setProc8(String proc8) {
		this.proc8 = proc8;
	}
	public String getProc9() {
		return proc9;
	}
	public void setProc9(String proc9) {
		this.proc9 = proc9;
	}
	public String getProc10() {
		return proc10;
	}
	public void setProc10(String proc10) {
		this.proc10 = proc10;
	}
	public int getIcdVersion() {
		return icdVersion;
	}
	public void setIcdVersion(int icdVersion) {
		this.icdVersion = icdVersion;
	}
	public String getDrg() {
		return drg;
	}
	public void setDrg(String drg) {
		this.drg = drg;
	}
	public int getDrgVersion() {
		return drgVersion;
	}
	public void setDrgVersion(int drgVersion) {
		this.drgVersion = drgVersion;
	}
	public int getDischargeStatus() {
		return dischargeStatus;
	}
	public void setDischargeStatus(int dischargeStatus) {
		this.dischargeStatus = dischargeStatus;
	}
	public String getUbRev() {
		return ubRev;
	}
	public void setUbRev(String ubRev) {
		this.ubRev = ubRev;
	}
	public String getTypeOfBill() {
		return typeOfBill;
	}
	public void setTypeOfBill(String typeOfBill) {
		this.typeOfBill = typeOfBill;
	}
	public String getUnits() {
		return units;
	}
	public void setUnits(String units) {
		this.units = units;
	}
	public String getPlaceOfService() {
		return placeOfService;
	}
	public void setPlaceOfService(String placeOfService) {
		this.placeOfService = placeOfService;
	}
	public boolean isDenied() {
		return denied;
	}
	public void setDenied(boolean denied) {
		this.denied = denied;
	}
	public String getProviderId() {
		return providerId;
	}
	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}
	public boolean isSupplemental() {
		return supplemental;
	}
	public void setSupplemental(boolean supplemental) {
		this.supplemental = supplemental;
	}
	
}
