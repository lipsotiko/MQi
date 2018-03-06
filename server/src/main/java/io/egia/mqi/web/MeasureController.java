package io.egia.mqi.web;

import org.springframework.web.bind.annotation.RestController;

import io.egia.mqi.domain.Measure;
import io.egia.mqi.service.MeasureService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class MeasureController {
	
	@Autowired
	private MeasureService measureService;
		
	@RequestMapping("/measures")
	public List<Measure> measure(@RequestParam(value="measure_id", defaultValue="all") String measureId) {
		if (measureId.equals("all")) {
			//if no measure id is passed in, return all measures
			return measureService.getMeasure();
		} else {
			//otherwise, return the requested measure
			return measureService.getMeasure(Long.parseLong(measureId));
		}
	}
	
	@RequestMapping("/process")
	public String process() {

		try {
			measureService.measureProcess();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "Measure Process";
	}
}