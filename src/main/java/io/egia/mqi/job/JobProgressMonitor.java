package io.egia.mqi.job;

import io.egia.mqi.chunk.ChunkRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static io.egia.mqi.chunk.ChunkStatus.PROCESSED;
import static io.egia.mqi.job.JobStatus.RUNNING;

@Service
class JobProgressMonitor {
    private Logger log = LoggerFactory.getLogger(JobProgressMonitor.class);

    private JobRepo jobRepo;
    private ChunkRepo chunkRepo;
    private SimpMessagingTemplate template;

    JobProgressMonitor(JobRepo jobRepo, ChunkRepo chunkRepo, SimpMessagingTemplate template) {
        this.jobRepo = jobRepo;
        this.chunkRepo = chunkRepo;
        this.template = template;
    }

    @Async
    void startMonitoringJob(Integer pollInterval, UUID jobId) {
        log.info(String.format("Started Job Progress Monitoring process on Job#: %s ", jobId));
        Long processedPatientsCount;

        Optional<Job> optionalJob = jobRepo.findById(jobId);
        while (optionalJob.isPresent()) {
            Job job = optionalJob.get();

            processedPatientsCount = chunkRepo.countByChunkStatus(PROCESSED);
            if (!job.getJobStatus().equals(RUNNING)) {
                break;
            }

            job.setProcessedPatientCount(processedPatientsCount);
            log.info(String.format("Job#: %s, Progress: %s", jobId, job.getProgress()));
            template.convertAndSend("/topic/job", job);

            optionalJob = jobRepo.findById(jobId);

            try {
                Thread.sleep(pollInterval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
