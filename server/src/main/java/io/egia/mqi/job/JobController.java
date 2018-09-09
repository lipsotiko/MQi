package io.egia.mqi.job;

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
    private MeasureService measureService;
    
    JobController(JobRepository jobRepository
            , JobMeasureRepository jobMeasureRepository
            , MeasureService measureService) {
        this.jobRepository = jobRepository;
        this.jobMeasureRepository = jobMeasureRepository;
        this.measureService = measureService;
    }

    @PostMapping("/process")
    public void process(@RequestBody List<Long> measureIds) throws UnknownHostException {
        Job job = Job.builder().status(Job.Status.PENDING).build();
        job = jobRepository.saveAndFlush(job);

        for(Long measureId: measureIds) {
            jobMeasureRepository.saveAndFlush(
                    JobMeasure.builder().jobId(job.getJobId()).measureId(measureId).build()
            );
        }

        measureService.process();
    }
}
