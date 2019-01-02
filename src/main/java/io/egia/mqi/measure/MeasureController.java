package io.egia.mqi.measure;

import io.egia.mqi.job.Job;
import io.egia.mqi.job.JobRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
public class MeasureController {
    private MeasureRepo measureRepo;
    private RuleParamRepo ruleParamRepo;

    @Autowired
    private JobRepo jobRepo;

    @Value("${mqi.properties.system.version}")
    private String systemVersion;

    MeasureController(MeasureRepo measureRepo, RuleParamRepo ruleParamRepo) {
        this.measureRepo = measureRepo;
        this.ruleParamRepo = ruleParamRepo;
    }

    @GetMapping("/measure")
    public Measure getMeasure(@RequestParam(value = "measureId") UUID measureId) {
        Optional<Measure> optionalMeasure = measureRepo.findById(measureId);
        return optionalMeasure.orElse(null);
    }

    @DeleteMapping("/measure")
    public void deleteMeasure(@RequestBody List<UUID> measureIds) {
        measureRepo.deleteAll(measureRepo.findAllById(measureIds));
    }

    @PutMapping("/measure")
    Measure putMeasure(@RequestBody Measure newMeasure) {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("America/New_York"));
        MeasureLogic measureLogic;
        if (newMeasure.getMeasureId() != null) {
            Optional<Measure> measure = measureRepo.findById(newMeasure.getMeasureId());
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
        List<MeasureListItem> measureListItems = measureRepo.findAllMeasureListItems();
        measureListItems.forEach(measureListItem -> {
            Optional<Job> optionalJob = jobRepo.findFirstByMeasureIdsOrderByLastUpdatedDesc(measureListItem.getMeasureId());
            optionalJob.ifPresent(job -> {
                if(job.getLastUpdated().isAfter(measureListItem.getMeasureLastUpdated())) {
                    measureListItem.setJobStatus(job.getJobStatus());
                    measureListItem.setJobLastUpdated(job.getLastUpdated());
                }
            });
        });
        return measureListItems;
    }

    @GetMapping("/rules_params")
    public Iterable<RuleParam> rulesParams() {
        return ruleParamRepo.findAll();
    }

}
