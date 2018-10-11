package io.egia.mqi.measure.helpers;

import io.egia.mqi.measure.RuleParam;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ParamHelper {

    public static String getText(List<RuleParam> ruleParams, String param) {
        RuleParam intParam = ruleParams.stream().filter(ruleParam ->
                ruleParam.getParamName().equals(param)).collect(Collectors.toList()).get(0);
        return intParam.getParamValue();
    }

    public static Date getDate(List<RuleParam> ruleParams, String param) throws ParseException {
        RuleParam dateParam = ruleParams.stream().filter(ruleParam ->
                ruleParam.getParamName().equals(param)).collect(Collectors.toList()).get(0);
        return new SimpleDateFormat("yyyyMMdd").parse(dateParam.getParamValue());
    }

    public static Integer getInt(List<RuleParam> ruleParams, String param) {
        RuleParam intParam = ruleParams.stream().filter(ruleParam ->
                ruleParam.getParamName().equals(param)).collect(Collectors.toList()).get(0);
        return Integer.valueOf(intParam.getParamValue());
    }
}
