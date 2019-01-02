package io.egia.mqi.measure.rules;

import io.egia.mqi.RuleTest;
import io.egia.mqi.measure.MeasureMetaData;
import io.egia.mqi.measure.MeasureWorkspace;
import io.egia.mqi.measure.RuleParam;
import io.egia.mqi.patient.Patient;
import io.egia.mqi.patient.PatientData;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static io.egia.mqi.helpers.Helpers.UUID1;
import static org.assertj.core.api.Assertions.assertThat;

@RuleTest
public class AgeWithinDateRangeTest {

    private AgeWithinDateRange ageWithinDateRange;
    private PatientData patientData;
    private List<RuleParam> ruleParams = new ArrayList<>();
    private MeasureMetaData measureMetaData = new MeasureMetaData();
    private MeasureWorkspace measureWorkspace;

    @Before
    public void setUp() {
        ageWithinDateRange = new AgeWithinDateRange();

        patientData = new PatientData(1L);

        RuleParam ruleParam1 = new RuleParam("", "FROM_AGE","");
        ruleParam1.setParamValue("28");
        ruleParams.add(ruleParam1);

        RuleParam ruleParam2 = new RuleParam("", "TO_AGE","");
        ruleParam2.setParamValue("32");
        ruleParams.add(ruleParam2);

        RuleParam ruleParam3 = new RuleParam("", "START_DATE","");
        ruleParam3.setParamValue("19850122");
        ruleParams.add(ruleParam3);

        RuleParam ruleParam4 = new RuleParam("", "END_DATE","");
        ruleParam4.setParamValue("19901109");
        ruleParams.add(ruleParam4);

        measureWorkspace = new MeasureWorkspace(1L, UUID1);
    }

    @Test
    public void turns_28_at_start_of_date_range() {
        Patient patient = new Patient();
        Date dob = new GregorianCalendar(1956, Calendar.APRIL, 28).getTime();
        patient.setDateOfBirth(dob);
        patientData.addPatientRecord(patient);
        ageWithinDateRange.evaluate(patientData, ruleParams, measureMetaData, measureWorkspace);
        assertThat(measureWorkspace.getContinueProcessing()).isEqualTo(true);
    }

    @Test
    public void turns_28_prior_to_start_of_date_range() {
        Patient patient = new Patient();
        Date dob = new GregorianCalendar(1955, Calendar.APRIL, 28).getTime();
        patient.setDateOfBirth(dob);
        patientData.addPatientRecord(patient);
        ageWithinDateRange.evaluate(patientData, ruleParams, measureMetaData, measureWorkspace);
        assertThat(measureWorkspace.getContinueProcessing()).isEqualTo(false);
    }

    @Test
    public void turns_32_at_end_of_date_range() {
        Patient patient = new Patient();
        Date dob = new GregorianCalendar(1958, Calendar.APRIL, 28).getTime();
        patient.setDateOfBirth(dob);
        patientData.addPatientRecord(patient);
        ageWithinDateRange.evaluate(patientData, ruleParams, measureMetaData, measureWorkspace);
        assertThat(measureWorkspace.getContinueProcessing()).isEqualTo(true);
    }

    @Test
    public void turns_32_after_the_end_of_date_range() {
        Patient patient = new Patient();
        Date dob = new GregorianCalendar(1957, Calendar.APRIL, 28).getTime();
        patient.setDateOfBirth(dob);
        patientData.addPatientRecord(patient);
        ageWithinDateRange.evaluate(patientData, ruleParams, measureMetaData, measureWorkspace);
        assertThat(measureWorkspace.getContinueProcessing()).isEqualTo(false);
    }

    @Test
    public void turns_28_on_leap_year_at_start_of_date_range() {
        Patient patient = new Patient();
        Date dob = new GregorianCalendar(1956, Calendar.FEBRUARY, 28).getTime();
        patient.setDateOfBirth(dob);
        patientData.addPatientRecord(patient);
        ageWithinDateRange.evaluate(patientData, ruleParams, measureMetaData, measureWorkspace);
        assertThat(measureWorkspace.getContinueProcessing()).isEqualTo(true);
    }

    @Test
    public void patients_with_null_dob_should_stop_being_processed() {
        Patient patient = new Patient();
        patientData.addPatientRecord(patient);
        ageWithinDateRange.evaluate(patientData, ruleParams, measureMetaData, measureWorkspace);
        assertThat(measureWorkspace.getContinueProcessing()).isEqualTo(false);
    }

    @Test
    public void date_string_that_throws_parse_exception_should_stop_processing() {
        Patient patient = new Patient();
        Date dob = new GregorianCalendar(1956, Calendar.FEBRUARY, 28).getTime();
        patient.setDateOfBirth(dob);
        patientData.addPatientRecord(patient);
        ruleParams.get(3).setParamValue("ABCDEFG");
        ageWithinDateRange.evaluate(patientData, ruleParams, measureMetaData, measureWorkspace);
        assertThat(measureWorkspace.getContinueProcessing()).isEqualTo(false);
    }
}
