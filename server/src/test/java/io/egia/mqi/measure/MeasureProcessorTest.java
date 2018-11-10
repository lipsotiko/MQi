package io.egia.mqi.measure;

import io.egia.mqi.job.JobService;
import io.egia.mqi.patient.Patient;
import io.egia.mqi.patient.PatientData;
import io.egia.mqi.visit.Visit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.*;

import static io.egia.mqi.helpers.Helpers.getMeasureFromResource;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MeasureProcessorTest {
    private List<Patient> patients = new ArrayList<>();
    private List<Visit> visits = new ArrayList<>();
    private MeasureProcessor subject;
    private List<Measure> measures = new ArrayList<>();
    private ZonedDateTime timeExecuted = ZonedDateTime.now();

    @Mock
    private JobService jobService;

    @Before
    public void setUp() throws IOException {

        subject = new MeasureProcessor(jobService);

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

        Measure measure = getMeasureFromResource("fixtures", "sampleMeasure2.json");
        measure.setMeasureId(1L);
        measures.add(measure);
    }

    @Test
    public void patient_data_hash_gets_built() {
        subject.process(measures, patients, visits, null, timeExecuted);
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
    public void clears_measure_processor() {
        subject.process(measures, patients, visits, null, timeExecuted);
        subject.clear();
        assertThat(subject.getPatientDataHash().size()).isEqualTo(0);
        assertThat(subject.getRulesEvaluatedCount()).isEqualTo(0);
    }

    @Test
    public void measure_with_null_logic_throws_measure_processor_exception() {
        Measure measure = new Measure();
        catchThrowableOfType(() ->
                subject.process(Collections.singletonList(measure), patients, visits, null, timeExecuted),
                MeasureProcessorException.class);
    }

    @Test
    public void measure_processor_exception_calls_job_service_fail_method() {
        Measure measure = new Measure();
        subject.process(Collections.singletonList(measure), patients, visits, null, timeExecuted);
        verify(jobService, times(1)).fail();
    }
}
