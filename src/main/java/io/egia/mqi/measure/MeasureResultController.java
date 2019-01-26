package io.egia.mqi.measure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
public class MeasureResultController {

    @Autowired
    private MeasureResultRepo measureResultRepo;

    @Autowired
    private RuleTraceRepo ruleTraceRepo;

    @GetMapping("/results_detail")
    public List<MeasureResult> getDetails(@RequestParam(value = "measureId") UUID measureId) {
        return measureResultRepo.findAllByMeasureId(measureId);
    }

    @GetMapping("/results_summary")
    public Map<String, Long> getSummary(@RequestParam(value = "measureId") UUID measureId) {
        List<MeasureResult> results = measureResultRepo.findAllByMeasureId(measureId);
        return results.stream().map(MeasureResult::getResultCode)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }

    @GetMapping("/rule_trace")
    public List<RuleTrace> getRuleTrace(@RequestParam(value = "measureId") UUID measureId,
                                        @RequestParam(value = "patientId") Long patientId) {
        return ruleTraceRepo.findAllByMeasureIdAndPatientId(measureId, patientId);
    }

}
