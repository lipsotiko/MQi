package io.egia.mqi.measure.rules;

import io.egia.mqi.measure.*;
import io.egia.mqi.patient.PatientData;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static io.egia.mqi.measure.helpers.ParamHelper.getDate;
import static io.egia.mqi.measure.helpers.ParamHelper.getInt;
import static java.util.Calendar.*;

@RuleParams(params = {
        @Param(name = "FROM_AGE", type = "INTEGER")
        , @Param(name = "TO_AGE", type = "INTEGER")
        , @Param(name = "START_DATE", type = "DATE")
        , @Param(name = "END_DATE", type = "DATE")
})
public class AgeWithinDateRange implements Rule {
    public MeasureWorkspace evaluate(PatientData patientData,
                                     List<RuleParam> ruleParams,
                                     MeasureMetaData measureMetaData,
                                     MeasureWorkspace measureWorkspace) {

        if (patientData.getPatient().getDateOfBirth() == null) {
            measureWorkspace.setContinueProcessing(false);
            return measureWorkspace;
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
            measureWorkspace.setContinueProcessing(false);
            return measureWorkspace;
        }

        Integer ageAtStartDate = getDiffYears(patientData.getPatient().getDateOfBirth(), startDate);
        Integer ageAtEndDate = getDiffYears(patientData.getPatient().getDateOfBirth(), endDate);

        if (ageAtStartDate.equals(fromAge) || toAge.equals(ageAtEndDate)) {
            return measureWorkspace;
        }

        measureWorkspace.setContinueProcessing(false);
        return measureWorkspace;
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

}
