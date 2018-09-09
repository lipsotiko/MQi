package io.egia.mqi.measure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@RestController
public class MeasureController {
    private MeasureRepository measureRepository;
    private RuleParamRepository ruleParamRepository;

    @Value("${mqi.properties.system.version}")
    private String systemVersion;

    MeasureController(MeasureRepository measureRepository
            , RuleParamRepository ruleParamRepository) {
        this.measureRepository = measureRepository;
        this.ruleParamRepository = ruleParamRepository;
    }

    @GetMapping("/measure")
    public Measure getMeasure(@RequestParam(value = "measureId") Long measureId) {
        Optional<Measure> optionalMeasure = measureRepository.findById(measureId);
        return optionalMeasure.orElse(null);
    }

    @DeleteMapping("/measure")
    public void deleteMeasure(@RequestParam(value = "measureId") Long measureId) {
        measureRepository.deleteById(measureId);
    }

    @PutMapping("/measure")
    public Measure putMeasure(@RequestBody Measure newMeasure) {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("America/New_York"));
        Optional<Measure> measure = measureRepository.findById(newMeasure.getMeasureId());
        MeasureLogic measureLogic;
        if (measure.isPresent()) {
            Measure existingMeasure = measure.get();
            if (existingMeasure.getMeasureLogic().equals(newMeasure.getMeasureLogic())) {
                existingMeasure.setMeasureName(newMeasure.getMeasureName());
                measureLogic = existingMeasure.getMeasureLogic();
                measureLogic.setDescription(newMeasure.getMeasureLogic().getDescription());
                measureLogic.setMinimumSystemVersion(systemVersion);
                existingMeasure.setMeasureJson(measureLogic);
                return measureRepository.saveAndFlush(existingMeasure);
            }
        }

        measureLogic = newMeasure.getMeasureLogic();
        measureLogic.setMinimumSystemVersion(systemVersion);
        newMeasure.setMeasureJson(measureLogic);
        newMeasure.setLastUpdated(now);
        return measureRepository.saveAndFlush(newMeasure);
    }

    @GetMapping("/measure_list")
    public List<MeasureListItem> getMeasureList() {
        return measureRepository.findAllMeasureListItems();
    }

    @GetMapping("/rules_params")
    public Iterable<RuleParam> rulesParams() {
        return ruleParamRepository.findAll();
    }


}
