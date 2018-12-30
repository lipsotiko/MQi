package io.egia.mqi.measure;

import io.egia.mqi.patient.Patient;
import io.egia.mqi.visit.Visit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.*;

import static io.egia.mqi.helpers.Helpers.getMeasureFromResource;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class MeasureProcessorTest {
    private List<Patient> patients = new ArrayList<>();
    private List<Visit> visits = new ArrayList<>();
    private MeasureProcessor subject;
    private List<Measure> measures = new ArrayList<>();
    private ZonedDateTime timeExecuted = ZonedDateTime.now();

    @Before
    public void setUp() throws IOException {

        subject = new MeasureProcessor();

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
    public void patient_data_hash_gets_built() throws Exception {
        subject.process(measures, patients, visits, null, timeExecuted, null);
        subject.getPatientDataHash().forEach((k, v) -> assertThat(v.getVisitCount()).isEqualTo(20));
        assertThat(subject.getPatientDataHash().size()).isEqualTo(5);
    }

    @Test
    public void measure_stepper_returns_number_of_rules_evaluated() throws Exception {
        subject.process(measures, patients, visits, null, timeExecuted, null);
        assertThat(subject.getRulesEvaluatedCount()).isEqualTo(15);
    }

    @Test
    public void clears_measure_processor() throws Exception {
        subject.process(measures, patients, visits, null, timeExecuted, null);
        subject.clear();
        assertThat(subject.getPatientDataHash().size()).isEqualTo(0);
        assertThat(subject.getRulesEvaluatedCount()).isEqualTo(0);
    }

    @Test
    public void measure_with_null_logic_throws_measure_processor_exception() {
        Measure measure = new Measure();
        catchThrowableOfType(() ->
                        subject.process(Collections.singletonList(measure), patients, visits, null, timeExecuted, null),
                MeasureProcessorException.class);
    }

}
