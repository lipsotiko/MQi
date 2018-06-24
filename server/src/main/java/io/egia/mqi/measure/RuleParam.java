package io.egia.mqi.measure;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
public class RuleParam {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    Long ruleParamId;

    private String ruleName;
    private String paramName;
    private String paramType;

    public RuleParam(String ruleName, String paramName, String paramType){
        this.ruleName = ruleName;
        this.paramName = paramName;
        this.paramType = paramType;
    }

    public RuleParam() {

    }
}
