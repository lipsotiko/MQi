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
    private List<PatientMeasureLog> patientMeasureLogs = new ArrayList<>();

    @Override
    public void process(List<Measure> measures,
                        List<Patient> patients,
                        List<Visit> visits,
                        MeasureMetaData measureMetaData,
                        ZonedDateTime timeExecuted) {

        processWasCalled = true;

        if (measures != null) {
            processWasCalledWithMeasures = measures;
        }

        if (patients != null) {
            processWasCalledWithPatients.addAll(patients);
        }

        if (visits != null) {
            processWasCalledWithVisits.addAll(visits);
        }

        if (measureMetaData != null) {
            processWasCalledWithMeasureMetaData = measureMetaData;
        }

        if (measures != null && patients != null) {
            for (Measure m : measures) {
                for (Patient p : patients) {
                    patientMeasureLogs.add(
                            PatientMeasureLog.builder()
                                    .measureId(m.getMeasureId())
                                    .patientId(p.getPatientId())
                                    .build()
                    );
                }
            }
        }

    }

    @Override
    public void clear() {
        clearWasCalled = true;
        processWasCalledWithMeasures = new ArrayList<>();
        processWasCalledWithPatients = new ArrayList<>();
    }

    @Override
    public List<PatientMeasureLog> getLog() {
        List<PatientMeasureLog> stubbedLogs = new ArrayList<>();

        for (Measure m : processWasCalledWithMeasures) {
            for (Patient p : processWasCalledWithPatients) {
                stubbedLogs.add(
                        PatientMeasureLog.builder()
                                .measureId(m.getMeasureId())
                                .patientId(p.getPatientId())
                                .build()
                );
            }

        }

        return stubbedLogs;
    }

    @Override
    public List<MeasureResult> getResults() {
        List<MeasureResult> stubbedResults = new ArrayList<>();

        for (Measure m : processWasCalledWithMeasures) {
            for (Patient p : processWasCalledWithPatients) {
                stubbedResults.add(
                        MeasureResult.builder()
                                .measureId(m.getMeasureId())
                                .patientId(p.getPatientId())
                                .resultCode("DENOMINATOR").build()
                );
            }
        }

        return stubbedResults;
    }

}
