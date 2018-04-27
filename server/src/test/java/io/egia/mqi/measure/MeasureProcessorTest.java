package io.egia.mqi.measure;

import io.egia.mqi.chunk.Chunk;
import io.egia.mqi.helpers.Helpers;
import io.egia.mqi.patient.Patient;
import io.egia.mqi.patient.PatientData;
import io.egia.mqi.visit.Visit;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class MeasureProcessorTest {
    private List<Patient> patients = new ArrayList<>();
    private List<Visit> visits = new ArrayList<>();

    private MeasureProcessor measureProcessor = new MeasureProcessor();
    private List<Measure> measures = new ArrayList<>();

    @Before
    public void setUp() throws IOException {
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

        Measure measure = Helpers.getMeasureFromResource("fixtures","sample.json");
        measures.add(measure);

        Long chunkId = 1L;

        measureProcessor.setChunkId(chunkId);
        measureProcessor.setMeasures(measures);
        measureProcessor.setPatientData(patients, visits);
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
        measureProcessor.iterateOverPatientsAndMeasures();
    }

    @Test
    public void clearMeasureWorkspace() {
        measureProcessor.clear();
        assertThat(measureProcessor.getPatientDataHash().size()).isEqualTo(0);
    }
}
