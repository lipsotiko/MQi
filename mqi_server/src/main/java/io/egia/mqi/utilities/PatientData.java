package io.egia.mqi.utilities;

import java.util.ArrayList;
import java.util.List;

import io.egia.mqi.domain.Patient;
import io.egia.mqi.domain.Visit;

/**
 * 
 * @author vango
 *
 *         The purpose of this class is to group all data related to 
 *         a single patient. This will make it easier to traverse
 *         patients within rules.
 *         
 */

public class PatientData {
	
	Long patientId;
	
	List<Patient> patientList = new ArrayList<Patient>();
	List<Visit> visitList = new ArrayList<Visit>();
	
	public void setPatientId(Long patientId) {
		this.patientId = patientId;
	}
	
	public void setPatientList (ArrayList<Patient> patientList) {
		this.patientList = patientList;
	}
	
	public void setVisitList (ArrayList<Visit> visitList) {
		this.visitList = visitList;
	}
}