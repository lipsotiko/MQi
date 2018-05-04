package io.egia.mqi.measure;

import io.egia.mqi.patient.Patient;
import io.egia.mqi.visit.Visit;
import lombok.Data;

import java.util.List;

@Data
public class MeasureProcessorSpy implements MeasureProcessor {

    private Long setChunkIdWasCalledWith;
    private List<Measure> setMeasuresWasCalledWith;
    private boolean processWasCalled = false;
    private boolean clearWasCalled = false;
    private List<Patient> setPatientDataWasCalledWithPatients;
    private List<Visit> setPatientDataWasCalledWithVisits;

    @Override
    public void setChunkId(Long chunkId) {
        setChunkIdWasCalledWith = chunkId;
    }

    @Override
    public void setMeasures(List<Measure> measures) {
        setMeasuresWasCalledWith = measures;
    }

    @Override
    public void setPatientData(List<Patient> patients, List<Visit> visits) {
        setPatientDataWasCalledWithPatients = patients;
        setPatientDataWasCalledWithVisits = visits;
    }

    @Override
    public void process() {
        processWasCalled = true;
    }

    @Override
    public void clear() {
        clearWasCalled = true;
    }
}
