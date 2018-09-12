package io.egia.mqi.measure;

import io.egia.mqi.chunk.Chunk;
import io.egia.mqi.helpers.Helpers;
import io.egia.mqi.patient.Patient;
import io.egia.mqi.patient.PatientData;
import io.egia.mqi.patient.PatientMeasureLogRepository;
import io.egia.mqi.visit.Visit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.*;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class MeasureProcessorImplTest {
    private List<Patient> patients = new ArrayList<>();
    private List<Visit> visits = new ArrayList<>();

    @Mock
    private PatientMeasureLogRepository patientMeasureLogRepository;
    private MeasureProcessorImpl subject;
    private List<Measure> measures = new ArrayList<>();

    @Before
    public void setUp() throws IOException {

        subject = new MeasureProcessorImpl(patientMeasureLogRepository);

        for (Long i = 1L; i <= 5; i++) {
            Patient p = new Patient();
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

        Measure measure = Helpers.getMeasureFromResource("fixtures", "sampleMeasure.json");
        measures.add(measure);
    }

    @Test
    public void validatePatientDataHash() {
        subject.process(measures, patients, visits);
        Hashtable<Long, PatientData> patientDataHash = subject.getPatientDataHash();
        Set<Long> keys = patientDataHash.keySet();
        Iterator<Long> itr = keys.iterator();
        Long patientId;

        int patientCount = subject.getPatientDataHash().size();
        assertThat(patientCount).isEqualTo(5);

        while (itr.hasNext()) {
            patientId = itr.next();
            assertThat(patientDataHash.get(patientId).getVisitCount()).isEqualTo(20);
        }
    }

    @Test
    public void processEvaluatesPatientData() {
        subject.process(measures, patients, visits);
        assertThat(subject.getMeasureResults().size()).isEqualTo(5);
        assertThat(subject.getRulesEvaluatedCount()).isEqualTo(15);
    }

    @Test
    public void clearMeasureWorkspace() {
        subject.process(measures, patients, visits);
        subject.clear();
        assertThat(subject.getPatientDataHash().size()).isEqualTo(0);
        assertThat(subject.getMeasures().size()).isEqualTo(0);
        assertThat(subject.getMeasureResults().size()).isEqualTo(0);
        assertThat(subject.getRulesEvaluatedCount()).isEqualTo(0);
    }
}
