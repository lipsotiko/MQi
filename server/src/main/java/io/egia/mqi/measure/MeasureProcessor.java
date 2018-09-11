package io.egia.mqi.measure;

import io.egia.mqi.patient.Patient;
import io.egia.mqi.visit.Visit;

import java.util.List;

public interface MeasureProcessor {
    void process(List<Measure> measures, List<Patient> patients, List<Visit> visits);
    void clear();
}
