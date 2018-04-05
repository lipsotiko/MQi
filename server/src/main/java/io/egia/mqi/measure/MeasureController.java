package io.egia.mqi.measure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MeasureController {

    @Autowired
    private MeasureService measureService;

    @RequestMapping("/measure")
    public List<Measure> measure(@RequestParam(value = "measure_id", defaultValue = "all") String measureId) {
        if (measureId.equals("all")) {
            return measureService.getMeasure();
        } else {
            return measureService.getMeasure(Long.parseLong(measureId));
        }
    }

    @RequestMapping("/measure_list")
    public List<MeasureListItem> measureList() {
        return measureService.getMeasureList();
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