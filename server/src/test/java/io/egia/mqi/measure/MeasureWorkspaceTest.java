package io.egia.mqi.measure;

import io.egia.mqi.chunk.Chunk;
import io.egia.mqi.patient.Patient;
import io.egia.mqi.patient.PatientData;
import io.egia.mqi.visit.Visit;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class MeasureWorkspaceTest {
    List<Patient> patients = new ArrayList<>();
    List<Visit> visits = new ArrayList<>();
    Chunk c = new Chunk();
    MeasureWorkspace measureWorkspace;

    @Before
    public void setUp() {
        for (Long i = 1L; i <= 5; i++) {
            Patient p = new Patient();
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
        measureWorkspace = new MeasureWorkspace(patients, visits);
    }

    @Test
    public void validatePatientDataHash() {
        Hashtable<Long, PatientData> patientDataHash = measureWorkspace.getPatientDataHash();
        Set<Long> keys = patientDataHash.keySet();
        Iterator<Long> itr = keys.iterator();
        Long lng;

        while (itr.hasNext()) {
            lng = itr.next();
            assertThat(patientDataHash.get(lng).getVisitCount()).isEqualTo(20);
        }
    }

    @Test
    public void getPatientCount() {
        int patientCount = measureWorkspace.getPatientDataHash().size();
        assertThat(patientCount).isEqualTo(5);
    }

    @Test
    public void clearMeasureWorkspace() {
        measureWorkspace.clear();
        int patientCount = measureWorkspace.getPatientDataHash().size();
        assertThat(patientCount).isEqualTo(0);
    }
}
