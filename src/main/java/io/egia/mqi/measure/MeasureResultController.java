package io.egia.mqi.measure;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
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

    @Data
    class MeasureSummary {
        String resultCode;
        Long count;
        MeasureSummary(String resultCode, Long count) {
            this.resultCode = resultCode;
            this.count = count;
        }
    }

    @GetMapping("/results_summary")
    public List<MeasureSummary> getSummary(@RequestParam(value = "measureId") UUID measureId) {
        List<MeasureResult> results = measureResultRepo.findAllByMeasureId(measureId);
        Map<String, Long> collect = results.stream().map(MeasureResult::getResultCode)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        List<MeasureSummary> measureSummaries = new ArrayList<>();
        collect.forEach((k,v) -> measureSummaries.add(new MeasureSummary(k, v)));
        return measureSummaries;
    }

    @GetMapping("/rule_trace")
    public List<RuleTrace> getRuleTrace(@RequestParam(value = "measureId") UUID measureId,
                                        @RequestParam(value = "patientId") Long patientId) {
        return ruleTraceRepo.findAllByMeasureIdAndPatientId(measureId, patientId);
    }

}
