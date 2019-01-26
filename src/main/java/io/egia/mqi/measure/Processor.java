package io.egia.mqi.measure;

import io.egia.mqi.patient.Patient;
import io.egia.mqi.patient.PatientMeasureLog;
import io.egia.mqi.visit.Visit;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public interface Processor {
    void process(List<Measure> measures,
                 List<Patient> patients,
                 List<Visit> visits,
                 MeasureMetaData measureMetaData,
                 ZonedDateTime timeExecuted,
                 UUID jobId) throws MeasureProcessorException;

    void clear();

    List<PatientMeasureLog> getLog();

    List<MeasureResult> getResults();

    List<RuleTrace> getRuleTrace();
}
