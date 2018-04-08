package io.egia.mqi.measure;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MeasureController {
    private MeasureService measureService;
    private MeasureRepository measureRepository;

    public MeasureController(MeasureService measureService, MeasureRepository measureRepository) {
        this.measureService = measureService;
        this.measureRepository = measureRepository;
    }

    @RequestMapping("/measure")
    public List<Measure> measure(@RequestParam(value = "measure_id", defaultValue = "all") String measureId) {
        if (measureId.equals("all")) {
            return measureRepository.findAll();
        } else {
            return measureRepository.findByMeasureId(Long.parseLong(measureId));
        }
    }

    @RequestMapping("/measure_list")
    public List<MeasureListItem> measureList() {
        return measureRepository.findAllMeasureListItems();
    }

    @RequestMapping("/process")
    public String process() {

        try {
            measureService.measureProcess();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return "measure Process";
    }
}
