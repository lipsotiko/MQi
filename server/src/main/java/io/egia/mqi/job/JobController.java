package io.egia.mqi.job;

import io.egia.mqi.measure.MeasureRepository;
import io.egia.mqi.measure.MeasureService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.UnknownHostException;
import java.util.List;

@RestController
public class JobController {

    private JobRepository jobRepository;
    private JobMeasureRepository jobMeasureRepository;
    private MeasureRepository measureRepository;
    private MeasureService measureService;

    JobController(JobRepository jobRepository
            , JobMeasureRepository jobMeasureRepository
            , MeasureRepository measureRepository
            , MeasureService measureService) {
        this.jobRepository = jobRepository;
        this.jobMeasureRepository = jobMeasureRepository;
        this.measureRepository = measureRepository;
        this.measureService = measureService;
    }

    @PostMapping("/process")
    public void process(@RequestBody List<Long> measureIds) throws UnknownHostException {
        Job job = jobRepository.saveAndFlush(Job.builder().jobStatus(JobStatus.RUNNING).build());

        for (Long measureId : measureIds) {
            jobMeasureRepository.saveAndFlush(
                    JobMeasure.builder().jobId(job.getJobId()).measureId(measureId).build()
            );
        }

        measureService.process(job, measureRepository.findAllById(measureIds));
    }
}
