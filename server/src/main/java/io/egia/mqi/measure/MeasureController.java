package io.egia.mqi.measure;

import org.springframework.web.bind.annotation.*;

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
    public Optional<Measure> getMeasure(@RequestParam(value = "measureName") String measureName) {
        return measureRepository.findByMeasureName(measureName);
    }

    @PutMapping("/measure")
    public Measure putMeasure(@RequestBody Measure measure) {
        measure.setMeasureJson(measure.getMeasureLogic());
        return measureRepository.save(measure);
    }

    @GetMapping("/measure_list")
    public List<MeasureListItem> getMeasureList() {
        return measureRepository.findAllMeasureListItems();
    }

    @GetMapping("/rules_params")
    public Iterable<RuleParam> rulesParams() {
        return ruleParamRepository.findAll();
    }

    @GetMapping("/process")
    public String process() {
        measureService.process();
        return "measure Process";
    }


}
