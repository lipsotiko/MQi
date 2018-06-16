package io.egia.mqi.measure;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Step {
    private int stepId;
    private String rule;

    @JsonIgnore
    private Map<String, String> parameters;
    private int successStepId;
    private int failureStepId;
}
