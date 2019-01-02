package io.egia.mqi.measure.rules;

import io.egia.mqi.RuleTest;
import io.egia.mqi.measure.MeasureWorkspace;
import io.egia.mqi.measure.RuleParam;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static io.egia.mqi.helpers.Helpers.UUID1;
import static org.assertj.core.api.Assertions.assertThat;


@RuleTest
public class SetResultCodeTest {

    private SetResultCode setResultCode;
    private List<RuleParam> ruleParams = new ArrayList<>();
    private MeasureWorkspace measureWorkspace;

    @Before
    public void setUp() {
        setResultCode = new SetResultCode();

        RuleParam ruleParam1 = new RuleParam("", "RESULT_CODE", "TEXT");
        ruleParam1.setParamValue("DENOMINATOR");
        ruleParams.add(ruleParam1);

        measureWorkspace = new MeasureWorkspace(1L, UUID1);
    }

    @Test
    public void result_code_is_set_to_denominator() {
        setResultCode.evaluate(null, ruleParams, null, measureWorkspace);
        assertThat(measureWorkspace.getResultCode()).isEqualTo("DENOMINATOR");
    }
}
