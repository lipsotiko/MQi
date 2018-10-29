package io.egia.mqi.job;

import io.egia.mqi.chunk.ChunkRepo;
import io.egia.mqi.chunk.ChunkStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
class JobProgressMonitor {
    private Logger log = LoggerFactory.getLogger(JobProgressMonitor.class);

    private JobRepo jobRepo;
    private ChunkRepo chunkRepo;

    JobProgressMonitor(JobRepo jobRepo, ChunkRepo chunkRepo) {
        this.jobRepo = jobRepo;
        this.chunkRepo = chunkRepo;
    }

    @Async
    void startMonitoringJob(Integer pollInterval, Long jobId) {
        log.info(String.format("Started Job Progress Monitoring process on Job#: %s ",jobId));
        Long processedPatientsCount;

        Optional<Job> optionalJob = jobRepo.findById(jobId);
        while (optionalJob.isPresent()) {
            Job job = optionalJob.get();
            processedPatientsCount = chunkRepo.countByChunkStatus(ChunkStatus.DONE);
            job.setProcessedPatientCount(processedPatientsCount);

            log.info(String.format("Job#: %s, Progress: %s",jobId, job.getProgress()));

            if(job.getInitialPatientCount().equals(processedPatientsCount)) {
                job.setJobStatus(JobStatus.DONE);
                jobRepo.save(job);
                return;
            }

            jobRepo.save(job);
            optionalJob = jobRepo.findById(jobId);
            try {
                Thread.sleep(pollInterval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
