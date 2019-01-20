package io.egia.mqi.measure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class MeasureResultController {

    @Autowired
    private MeasureResultRepo measureResultRepo;

    @GetMapping("/measure_result_detail")
    public List<MeasureResult> getResults(@RequestParam(value = "measureId") UUID measureId) {
        return measureResultRepo.findAllByMeasureId(measureId);
    }

}
