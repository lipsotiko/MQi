package io.egia.mqi.measure;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Step {
    private int stepId;
    private String ruleName;
    private List<RuleParam> parameters;
    private int successStepId;
    private int failureStepId;
}
