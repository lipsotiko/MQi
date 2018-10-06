package io.egia.mqi.measure;

import io.egia.mqi.helpers.Helpers;
import io.egia.mqi.patient.Patient;
import io.egia.mqi.patient.PatientData;
import io.egia.mqi.patient.PatientMeasureLog;
import io.egia.mqi.patient.PatientMeasureLogRepo;
import io.egia.mqi.visit.Visit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.*;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MeasureProcessorImplTest {
    private List<Patient> patients = new ArrayList<>();
    private List<Visit> visits = new ArrayList<>();

    @Mock
    private PatientMeasureLogRepo patientMeasureLogRepo;
    private MeasureProcessorImpl subject;
    private List<Measure> measures = new ArrayList<>();

    private ZonedDateTime timeExecuted = ZonedDateTime.now();

    @Before
    public void setUp() throws IOException {

        subject = new MeasureProcessorImpl(patientMeasureLogRepo);

        for (Long i = 1L; i <= 5; i++) {
            Patient p = new Patient();
            p.setPatientId(i);
            p.setFirstName("vango");
            p.setDateOfBirth(new GregorianCalendar(1986, Calendar.APRIL, 28).getTime());
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
        measure.setMeasureId(1L);
        measures.add(measure);
    }

    @Test
    public void validatePatientDataHash() {
        subject.process(measures, patients, visits, timeExecuted);
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
        subject.process(measures, patients, visits, timeExecuted);
        assertThat(subject.getMeasureResults().size()).isEqualTo(5);
        assertThat(subject.getRulesEvaluatedCount()).isEqualTo(15);

        for(Long i = 1L; i <= 5L; i++) {
            verify(patientMeasureLogRepo, times(1)).deleteByPatientIdAndMeasureId(i, 1L);
            verify(patientMeasureLogRepo, times(1))
                    .save(PatientMeasureLog.builder().patientId(i).measureId(1L).lastUpdated(timeExecuted).build());
        }
    }

    @Test
    public void clearMeasureWorkspace() {
        subject.process(measures, patients, visits, timeExecuted);
        subject.clear();
        assertThat(subject.getPatientDataHash().size()).isEqualTo(0);
        assertThat(subject.getMeasures().size()).isEqualTo(0);
        assertThat(subject.getMeasureResults().size()).isEqualTo(0);
        assertThat(subject.getRulesEvaluatedCount()).isEqualTo(0);
    }
}
