package io.egia.mqi.measure;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
class Step {
    private int stepId;
    private String ruleName;
    private List<RuleParam> parameters;
    private int successStepId;
    private int failureStepId;
}
