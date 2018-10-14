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

        measureWorkspace = new MeasureWorkspace(1L, 11L);
    }

    @Test
    public void turns28AtStartOfDateRange() {
        Patient patient = new Patient();
        Date dob = new GregorianCalendar(1956, Calendar.APRIL, 28).getTime();
        patient.setDateOfBirth(dob);
        patientData.addPatientRecord(patient);
        ageWithinDateRange.evaluate(patientData, ruleParams, measureMetaData, measureWorkspace);
        assertThat(measureWorkspace.getContinueProcessing()).isEqualTo(true);
    }

    @Test
    public void turns28PriorToStartOfDateRange() {
        Patient patient = new Patient();
        Date dob = new GregorianCalendar(1955, Calendar.APRIL, 28).getTime();
        patient.setDateOfBirth(dob);
        patientData.addPatientRecord(patient);
        ageWithinDateRange.evaluate(patientData, ruleParams, measureMetaData, measureWorkspace);
        assertThat(measureWorkspace.getContinueProcessing()).isEqualTo(false);
    }

    @Test
    public void turns32AtEndOfDateRange() {
        Patient patient = new Patient();
        Date dob = new GregorianCalendar(1958, Calendar.APRIL, 28).getTime();
        patient.setDateOfBirth(dob);
        patientData.addPatientRecord(patient);
        ageWithinDateRange.evaluate(patientData, ruleParams, measureMetaData, measureWorkspace);
        assertThat(measureWorkspace.getContinueProcessing()).isEqualTo(true);
    }

    @Test
    public void turns32AfterTheEndOfDateRange() {
        Patient patient = new Patient();
        Date dob = new GregorianCalendar(1957, Calendar.APRIL, 28).getTime();
        patient.setDateOfBirth(dob);
        patientData.addPatientRecord(patient);
        ageWithinDateRange.evaluate(patientData, ruleParams, measureMetaData, measureWorkspace);
        assertThat(measureWorkspace.getContinueProcessing()).isEqualTo(false);
    }

    @Test
    public void turns28OnLeapYearAtStartOfDateRange() {
        Patient patient = new Patient();
        Date dob = new GregorianCalendar(1956, Calendar.FEBRUARY, 28).getTime();
        patient.setDateOfBirth(dob);
        patientData.addPatientRecord(patient);
        ageWithinDateRange.evaluate(patientData, ruleParams, measureMetaData, measureWorkspace);
        assertThat(measureWorkspace.getContinueProcessing()).isEqualTo(true);
    }

    @Test
    public void patientsWithNullDobShouldStopBeingProcessed() {
        Patient patient = new Patient();
        patientData.addPatientRecord(patient);
        ageWithinDateRange.evaluate(patientData, ruleParams, measureMetaData, measureWorkspace);
        assertThat(measureWorkspace.getContinueProcessing()).isEqualTo(false);
    }

    @Test
    public void dateStringThatThrowsParseExceptionShouldStopBeingProcessed() {
        Patient patient = new Patient();
        Date dob = new GregorianCalendar(1956, Calendar.FEBRUARY, 28).getTime();
        patient.setDateOfBirth(dob);
        patientData.addPatientRecord(patient);
        ruleParams.get(3).setParamValue("ABCDEFG");
        ageWithinDateRange.evaluate(patientData, ruleParams, measureMetaData, measureWorkspace);
        assertThat(measureWorkspace.getContinueProcessing()).isEqualTo(false);
    }
}
