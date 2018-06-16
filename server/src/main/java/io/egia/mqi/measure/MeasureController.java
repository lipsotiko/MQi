package io.egia.mqi.measure;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class MeasureController {
    private MeasureService measureService;
    private MeasureRepository measureRepository;

    public MeasureController(MeasureService measureService, MeasureRepository measureRepository) {
        this.measureService = measureService;
        this.measureRepository = measureRepository;
    }

    @RequestMapping("/measures")
    public Iterable<Measure> measures() {
        return measureRepository.findAll();
    }

    @RequestMapping("/measure")
    public Optional<Measure> measure(@RequestParam(value = "id") Long id) {
        return measureRepository.findById(id);
    }

    @RequestMapping("/measure_list")
    public List<MeasureListItem> measureList() {
        return measureRepository.findAllMeasureListItems();
    }

    @RequestMapping("/process")
    public String process() {
        measureService.process();
        return "measure Process";
    }
}
