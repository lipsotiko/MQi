package io.egia.mqi.measure;

import io.egia.mqi.patient.Patient;
import io.egia.mqi.patient.PatientMeasureLog;
import io.egia.mqi.visit.Visit;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProcessorSpy implements Processor {

    List<Measure> processWasCalledWithMeasures = new ArrayList<>();
    boolean processWasCalled = false;
    boolean clearWasCalled = false;
    List<Patient> processWasCalledWithPatients = new ArrayList<>();
    List<Visit> processWasCalledWithVisits = new ArrayList<>();
    MeasureMetaData processWasCalledWithMeasureMetaData;

    @Override
    public void process(List<Measure> measures,
                        List<Patient> patients,
                        List<Visit> visits,
                        MeasureMetaData measureMetaData,
                        ZonedDateTime timeExecuted) {

        processWasCalled = true;

        if(measures != null) {
            processWasCalledWithMeasures = measures;
        }

        if (patients != null){
            processWasCalledWithPatients.addAll(patients);
        }

        if (visits != null) {
            processWasCalledWithVisits.addAll(visits);
        }

        if (measureMetaData != null) {
            processWasCalledWithMeasureMetaData = measureMetaData;
        }


    }

    @Override
    public void clear() {
        clearWasCalled = true;
    }

    @Override
    public List<PatientMeasureLog> getLog() {
        return null;
    }

    @Override
    public List<MeasureResult> getResults() {
        List<MeasureResult> stubbedResults = new ArrayList<>();
        stubbedResults.add(MeasureResult.builder().patientId(1L).measureId(1L).resultCode("DENOMINATOR").build());
        return stubbedResults;
    }

}
