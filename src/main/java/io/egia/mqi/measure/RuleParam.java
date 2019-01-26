package io.egia.mqi.measure;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class RuleParam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long ruleParamId;

    private String ruleName;
    private String paramName;
    private String paramType;
    @Transient
    private String paramValue;

    public RuleParam(String ruleName, String paramName, String paramType) {
        this.ruleName = ruleName;
        this.paramName = paramName;
        this.paramType = paramType;
    }

    RuleParam() {

    }
}
