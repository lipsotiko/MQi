package io.egia.mqi.measure.rules;

import io.egia.mqi.measure.*;
import io.egia.mqi.patient.PatientData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Calendar.DATE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

@RuleParameters(params={
        @Param(name="FROM_AGE", type = "INTEGER")
        , @Param(name="TO_AGE", type = "INTEGER")
        , @Param(name="START_DATE", type = "DATE")
        , @Param(name="END_DATE", type = "DATE")
})
public class AgeWithinDateRange implements Rule {
    public MeasureResult evaluate(PatientData patientData,
                                  List<RuleParam> ruleParams,
                                  MeasureMetaData measureMetaData,
                                  MeasureResult measureResult) {

        if(patientData.getPatient().getDateOfBirth() == null) {
            measureResult.setContinueProcessing(false);
            return measureResult;
        }

        Integer fromAge = getInt(ruleParams, "FROM_AGE");
        Integer toAge = getInt(ruleParams, "TO_AGE");
        Date startDate;
        Date endDate;

        try {
            startDate = getDate(ruleParams, "START_DATE");
            endDate = getDate(ruleParams, "END_DATE");
        } catch (ParseException e) {
            e.printStackTrace();
            measureResult.setContinueProcessing(false);
            return measureResult;
        }

        Integer ageAtStartDate = getDiffYears(patientData.getPatient().getDateOfBirth(), startDate);
        Integer ageAtEndDate = getDiffYears(patientData.getPatient().getDateOfBirth(), endDate);

        if(ageAtStartDate.equals(fromAge) || toAge.equals(ageAtEndDate)) {
            return measureResult;
        }

        measureResult.setContinueProcessing(false);
        return measureResult;
    }

    private static int getDiffYears(Date first, Date last) {
        Calendar a = getCalendar(first);
        Calendar b = getCalendar(last);
        int diff = b.get(YEAR) - a.get(YEAR);
        if (a.get(MONTH) > b.get(MONTH) ||
                (a.get(MONTH) == b.get(MONTH) && a.get(DATE) > b.get(DATE))) {
            diff--;
        }
        return diff;
    }

    private static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance(Locale.US);
        cal.setTime(date);
        return cal;
    }

    private Date getDate(List<RuleParam> ruleParams, String param) throws ParseException {
        RuleParam dateParam = ruleParams.stream().filter(ruleParam ->
                ruleParam.getParamName().equals(param)).collect(Collectors.toList()).get(0);
        return new SimpleDateFormat("yyyyMMdd").parse(dateParam.getParamValue());
    }

    private Integer getInt(List<RuleParam> ruleParams, String param) {
        RuleParam intParam = ruleParams.stream().filter(ruleParam ->
                ruleParam.getParamName().equals(param)).collect(Collectors.toList()).get(0);
        return Integer.valueOf(intParam.getParamValue());
    }
}
