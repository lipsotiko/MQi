package io.egia.mqi.job;

import io.egia.mqi.measure.MeasureRepository;
import io.egia.mqi.measure.MeasureService;
import io.egia.mqi.server.Server;
import io.egia.mqi.server.ServerService;
import io.egia.mqi.server.SystemType;
import org.springframework.beans.factory.annotation.Value;
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
    private ServerService serverService;

    @Value("${server.port}")
    private String serverPort;

    JobController(JobRepository jobRepository
            , JobMeasureRepository jobMeasureRepository
            , MeasureRepository measureRepository
            , MeasureService measureService
            , ServerService serverService) {
        this.jobRepository = jobRepository;
        this.jobMeasureRepository = jobMeasureRepository;
        this.measureRepository = measureRepository;
        this.measureService = measureService;
        this.serverService = serverService;
    }

    @PostMapping("/process")
    public void process(@RequestBody List<Long> measureIds) throws UnknownHostException {

        Server server = serverService.getServerFromHostNameAndPort(serverPort);
        if (server.getSystemType().equals(SystemType.PRIMARY)) {
            Job job = jobRepository.saveAndFlush(Job.builder().jobStatus(JobStatus.RUNNING).build());
            for (Long measureId : measureIds) {
                jobMeasureRepository.saveAndFlush(
                        JobMeasure.builder().jobId(job.getJobId()).measureId(measureId).build()
                );
            }

            measureService.process(server, job, measureRepository.findAllById(measureIds));
            //TODO: Trigger call to other servers that will help process data
        }
    }
}
