package io.egia.mqi.measure;

import java.util.ArrayList;
import java.util.List;

public class MeasureWorkspace {
    private Long patientId;
    private Long measureId;
    private boolean continueProcessing = true;
    private List<String> ruleTrace = new ArrayList<>();
    private String resultCode;

    public MeasureWorkspace(Long patientId, Long measureId) {
        this.patientId = patientId;
        this.measureId = measureId;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getMeasureId() {
        return measureId;
    }

    public void setMeasureId(Long measureId) {
        this.measureId = measureId;
    }

    public boolean getContinueProcessing() {
        return this.continueProcessing;
    }

    public void setContinueProcessing(boolean continueProcessing) {
        this.continueProcessing = continueProcessing;
    }

    void writeRuleTrace(String rule) {
        ruleTrace.add(rule);
    }

    List<String> getRuleTrace() {
        return ruleTrace;
    }

    public String getResultCode() {
        return this.resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }
}
