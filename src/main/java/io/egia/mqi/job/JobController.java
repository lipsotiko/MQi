package io.egia.mqi.job;

import io.egia.mqi.chunk.ChunkService;
import io.egia.mqi.measure.MeasureProcessorException;
import io.egia.mqi.measure.MeasureRepo;
import io.egia.mqi.measure.MeasureService;
import io.egia.mqi.measure.MeasureServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

import static io.egia.mqi.job.JobStatus.DONE;
import static io.egia.mqi.job.JobStatus.FAILURE;
import static io.egia.mqi.job.JobStatus.RUNNING;

@RestController
public class JobController {
    private Logger log = LoggerFactory.getLogger(JobController.class);

    private JobRepo jobRepo;
    private MeasureRepo measureRepo;
    private ChunkService chunkService;
    private MeasureService measureService;
    private JobProgressMonitor jobProgressMonitor;

    JobController(JobRepo jobRepo,
                  MeasureRepo measureRepo,
                  ChunkService chunkService,
                  MeasureService measureService,
                  JobProgressMonitor jobProgressMonitor) {
        this.jobRepo = jobRepo;
        this.measureRepo = measureRepo;
        this.chunkService = chunkService;
        this.measureService = measureService;
        this.jobProgressMonitor = jobProgressMonitor;
    }

    @Async
    @PostMapping("/process")
    public void process(@RequestBody List<Long> measureIds) {
        Job job = jobRepo.saveAndFlush(
                Job.builder()
                        .jobStatus(RUNNING)
                        .measureIds(measureIds)
                        .startTime(new Date()).build());
        log.info(String.format("Started processing Job#: %s ", job.getId()));
        chunkService.chunkData(job);
        jobProgressMonitor.startMonitoringJob(1000, job.getId());
        try {
            measureService.process(measureRepo.findAllById(measureIds), job.getId());
        } catch (MeasureServiceException e) {
            job.setJobStatus(FAILURE);
            jobRepo.saveAndFlush(job);
            log.info(String.format("Failure while processing Job#: %s ", job.getId()));
            return;
        }
        job.setJobStatus(DONE);
        jobRepo.saveAndFlush(job);
        log.info(String.format("Completed processing Job#: %s ", job.getId()));
    }

}
