package io.egia.mqi.utilities;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import io.egia.mqi.domain.*;

/**
 * 
 * @author vango
 *
 *         The purpose of this class is to make all patient data within 
 *         a chunk available to rules for the purposes of applying measure
 *         logic and storing measure results.
 *         
 */

public class MeasureWorkspace {
	
	private Hashtable<Long, Hashtable<Long, Patient>> patientHash = new Hashtable <Long, Hashtable<Long, Patient>>();
	private Hashtable<Long, Hashtable<Long, Visit>> visitHash = new Hashtable <Long, Hashtable<Long, Visit>>();
	
	private ArrayList<PatientData> patientDataList = new ArrayList<PatientData>();
	
	public MeasureWorkspace(List<Patient> patients, List<Visit> visits) {
		
		//Group records by patient
		for (Patient p : patients) {
			Hashtable<Long, Patient> tmpPatientHash = patientHash.get(p.getPatientId());
			if (tmpPatientHash == null) {
				tmpPatientHash = new Hashtable<Long, Patient>();
				patientHash.put(p.getPatientId(), tmpPatientHash);
			}
			tmpPatientHash.put(p.getPatientId(), p);
		}
		
		for (Visit v : visits) {
			Hashtable<Long, Visit> tmpVisittHash = visitHash.get(v.getPatientId());
			if (tmpVisittHash == null) {
				tmpVisittHash = new Hashtable<Long, Visit>();
				visitHash.put(v.getPatientId(), tmpVisittHash);
			}
			tmpVisittHash.put(v.getVisitId(), v);
		}
		
		//Build patient data list
		Set<Long> keys = patientHash.keySet();
		for (Long key : keys) {
			PatientData patientData = new PatientData();
			patientData.setPatientId(key);
			if (patientHash.containsKey(key) && !patientHash.get(key).values().isEmpty() && (patientHash.get(key) != null)) {
				patientData.setPatientList(new ArrayList<Patient>(patientHash.get(key).values()));
			}
			if (visitHash.containsKey(key) && !visitHash.get(key).values().isEmpty() && (visitHash.get(key) != null)) {
				patientData.setVisitList(new ArrayList<Visit>(visitHash.get(key).values()));
			}
			patientDataList.add(patientData);
		}
	}
	
	public Hashtable<Long, Hashtable<Long, Patient>> getPatientHash() {
		return this.patientHash;
	}
	
	public Integer getPatientCount() {
		return patientDataList.size();
	}
	
	public void clearMeasureWorkspace(){
		patientHash.clear();
		visitHash.clear();
		patientDataList.clear();
	}
}