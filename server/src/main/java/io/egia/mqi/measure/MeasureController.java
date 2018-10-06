package io.egia.mqi.measure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@RestController
public class MeasureController {
    private MeasureRepo measureRepo;
    private RuleParamRepo ruleParamRepo;

    @Value("${mqi.properties.system.version}")
    private String systemVersion;

    MeasureController(MeasureRepo measureRepo, RuleParamRepo ruleParamRepo) {
        this.measureRepo = measureRepo;
        this.ruleParamRepo = ruleParamRepo;
    }

    @GetMapping("/measure")
    public Measure getMeasure(@RequestParam(value = "measureId") Long measureId) {
        Optional<Measure> optionalMeasure = measureRepo.findById(measureId);
        return optionalMeasure.orElse(null);
    }

    @DeleteMapping("/measure")
    public void deleteMeasure(@RequestParam(value = "measureId") Long measureId) {
        measureRepo.deleteById(measureId);
    }

    @PutMapping("/measure")
    public Measure putMeasure(@RequestBody Measure newMeasure) {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("America/New_York"));
        Optional<Measure> measure = measureRepo.findById(newMeasure.getMeasureId());
        MeasureLogic measureLogic;
        if (measure.isPresent()) {
            Measure existingMeasure = measure.get();
            if (existingMeasure.getMeasureLogic().equals(newMeasure.getMeasureLogic())) {
                existingMeasure.setMeasureName(newMeasure.getMeasureName());
                measureLogic = existingMeasure.getMeasureLogic();
                measureLogic.setDescription(newMeasure.getMeasureLogic().getDescription());
                measureLogic.setMinimumSystemVersion(systemVersion);
                existingMeasure.setMeasureJson(measureLogic);
                return measureRepo.saveAndFlush(existingMeasure);
            }
        }

        measureLogic = newMeasure.getMeasureLogic();
        measureLogic.setMinimumSystemVersion(systemVersion);
        newMeasure.setMeasureJson(measureLogic);
        newMeasure.setLastUpdated(now);
        return measureRepo.saveAndFlush(newMeasure);
    }

    @GetMapping("/measure_list")
    public List<MeasureListItem> getMeasureList() {
        return measureRepo.findAllMeasureListItems();
    }

    @GetMapping("/rules_params")
    public Iterable<RuleParam> rulesParams() {
        return ruleParamRepo.findAll();
    }

}
