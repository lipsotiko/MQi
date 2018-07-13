package io.egia.mqi.measure;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class MeasureController {
    private MeasureService measureService;
    private MeasureRepository measureRepository;
    private RuleParamRepository ruleParamRepository;

    public MeasureController(MeasureService measureService
                , MeasureRepository measureRepository
                , RuleParamRepository ruleParamRepository) {
        this.measureService = measureService;
        this.measureRepository = measureRepository;
        this.ruleParamRepository = ruleParamRepository;
    }

    @GetMapping("/measure")
    public Optional<Measure> measure(@RequestParam(value = "id") Long id) {
        return measureRepository.findById(id);
    }

    @GetMapping("/measure_list")
    public List<MeasureListItem> measureList() {
        return measureRepository.findAllMeasureListItems();
    }

    @GetMapping("/process")
    public String process() {
        measureService.process();
        return "measure Process";
    }

    @GetMapping("/rule_params")
    public Iterable<RuleParam> ruleParams() {
        return ruleParamRepository.findAll();
    }

    @GetMapping("/rules")
    public Iterable<String> ruleNames() {
        return ruleParamRepository.findAllDistinctRules();
    }

}
