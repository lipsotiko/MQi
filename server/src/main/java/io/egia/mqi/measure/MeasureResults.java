package io.egia.mqi.measure;

import java.util.ArrayList;
import java.util.List;

public class MeasureResults {

    private boolean continueProcessing = true;
    private boolean denominator;
    private List<String> ruleTrace = new ArrayList<>();

    public boolean getContinueProcessing() {
        return this.continueProcessing;
    }

    public void setContinueProcessing(boolean continueProcessing) {
        this.continueProcessing = continueProcessing;
    }

    public void setResults(boolean denominator) {
        this.denominator = denominator;
    }

    public void writeRuleTrace(String rule) {
        ruleTrace.add(rule);
    }

    public void clear() {
        continueProcessing = true;
        denominator = false;
        ruleTrace.clear();
    }
}
