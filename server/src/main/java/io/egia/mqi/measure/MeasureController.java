package io.egia.mqi.measure;

import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

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
    public Measure getMeasure(@RequestParam(value = "measureId") Long measureId) {
        Optional<Measure> optionalMeasure = measureRepository.findById(measureId);
        if (optionalMeasure.isPresent()) {
            return optionalMeasure.get();
        }
        return null;
    }

    @DeleteMapping("/measure")
    public void deleteMeasure(@RequestParam(value = "measureId") Long measureId) {
        measureRepository.deleteById(measureId);
    }

    @PutMapping("/measure")
    public Measure putMeasure(@RequestBody Measure measure) {

        //TODO: Update timestamp only when core measure logic changes

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("America/New_York"));
        measure.setMeasureJson(measure.getMeasureLogic());
        measure.setLastUpdated(now);
        return measureRepository.saveAndFlush(measure);
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
