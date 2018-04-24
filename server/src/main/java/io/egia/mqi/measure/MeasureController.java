package io.egia.mqi.measure;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.UnknownHostException;
import java.util.List;

@RestController
public class MeasureController {
    private MeasureService measureService;
    private MeasureRepository measureRepository;

    public MeasureController(MeasureService measureService, MeasureRepository measureRepository) {
        this.measureService = measureService;
        this.measureRepository = measureRepository;
    }

    @RequestMapping("/measures")
    public List<Measure> measures() {
        return measureRepository.findAll();
    }

    @RequestMapping("/measure")
    public Measure measure(@RequestParam(value = "measure_id") String measureId) {
        return measureRepository.findOneByMeasureId(Long.parseLong(measureId));
    }

    @RequestMapping("/measure_list")
    public List<MeasureListItem> measureList() {
        return measureRepository.findAllMeasureListItems();
    }

    @RequestMapping("/process")
    public String process() throws UnknownHostException {
        measureService.measureProcess();
        return "measure Process";
    }
}
