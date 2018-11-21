package io.egia.mqi.job;

import io.egia.mqi.chunk.ChunkService;
import io.egia.mqi.measure.MeasureRepo;
import io.egia.mqi.measure.MeasureService;
import io.egia.mqi.server.Server;
import io.egia.mqi.server.ServerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

import static io.egia.mqi.job.JobStatus.RUNNING;
import static io.egia.mqi.server.SystemType.PRIMARY;

@RestController
public class JobController {
    private Logger log = LoggerFactory.getLogger(JobController.class);

    private JobRepo jobRepo;
    private MeasureRepo measureRepo;
    private ChunkService chunkService;
    private MeasureService measureService;
    private ServerService serverService;
    private JobProgressMonitor jobProgressMonitor;

    @Value("${server.port}")
    private String serverPort;

    JobController(JobRepo jobRepo,
                  MeasureRepo measureRepo,
                  ChunkService chunkService,
                  MeasureService measureService,
                  ServerService serverService,
                  JobProgressMonitor jobProgressMonitor) {
        this.jobRepo = jobRepo;
        this.measureRepo = measureRepo;
        this.chunkService = chunkService;
        this.measureService = measureService;
        this.serverService = serverService;
        this.jobProgressMonitor = jobProgressMonitor;
    }

    @Async
    @PostMapping("/process")
    public void process(@RequestBody List<Long> measureIds) throws UnknownHostException {
        Server server = serverService.getServerFromHostNameAndPort(serverPort);

        if (server.getSystemType().equals(PRIMARY)) {
            Job job = jobRepo.save(
                    Job.builder()
                            .jobStatus(RUNNING)
                            .measureIds(measureIds)
                            .startTime(new Date()).build());
            log.info( String.format("Started processing Job#: %s ", job.getId()));
            chunkService.chunkData(job);
            jobProgressMonitor.startMonitoringJob(1000, job.getId());
            measureService.process(server, measureRepo.findAllById(measureIds), job.getId());
            log.info( String.format("Completed processing Job#: %s ", job.getId()));
        }
    }

}
