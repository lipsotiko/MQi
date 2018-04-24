package io.egia.mqi.measure;

import io.egia.mqi.chunk.Chunk;
import io.egia.mqi.patient.Patient;
import io.egia.mqi.patient.PatientData;
import io.egia.mqi.visit.Visit;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class MeasureProcessorTest {
    List<Patient> patients = new ArrayList<>();
    List<Visit> visits = new ArrayList<>();

    Long chunkId = 1L;

    @Autowired
    MeasureRepository measureRepository;
    MeasureProcessor measureProcessor;
    List<Measure> measures = new ArrayList<>();

    @Before
    public void setUp() {
        for (Long i = 1L; i <= 5; i++) {
            Patient p = new Patient();
            Chunk c = new Chunk();
            p.setChunk(c);
            p.setPatientId(i);
            p.setFirstName("vango");
            patients.add(p);
        }

        for (Long i = 1L; i <= 5; i++) {
            for (int j = 1; j <= 20; j++) {
                Visit v = new Visit();
                v.setPatientId(i);
                visits.add(v);
            }
        }

        for (Integer i = 1; i <= 3; i++) {
            Measure measure = new Measure();
            measure.setMeasureName("Measure " + i.toString() + ".json");
            measures.add(measure);
        }

        measureProcessor = new MeasureProcessor(chunkId, measures, patients, visits);
    }

    @Test
    public void validatePatientDataHash() {
        Hashtable<Long, PatientData> patientDataHash = measureProcessor.getPatientDataHash();
        Set<Long> keys = patientDataHash.keySet();
        Iterator<Long> itr = keys.iterator();
        Long patientId;

        int patientCount = measureProcessor.getPatientDataHash().size();
        assertThat(patientCount).isEqualTo(5);

        while (itr.hasNext()) {
            patientId = itr.next();
            assertThat(patientDataHash.get(patientId).getVisitCount()).isEqualTo(20);
        }
    }

    @Test
    public void callProcess(){
        measureProcessor.process();
    }

    @Test
    public void clearMeasureWorkspace() {
        measureProcessor.clear();
        assertThat(measureProcessor.getPatientDataHash().size()).isEqualTo(0);
    }
}
