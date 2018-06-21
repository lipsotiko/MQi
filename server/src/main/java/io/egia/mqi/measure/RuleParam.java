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
    private int displayOrder;

    public RuleParam(String ruleName, String paramName, String paramType, int displayOrder){
        this.ruleName = ruleName;
        this.paramName = paramName;
        this.paramType = paramType;
        this.displayOrder = displayOrder;
    }

    public RuleParam() {

    }
}
