package io.egia.mqi.job;

import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static io.egia.mqi.job.JobStatus.FAILURE;
import static io.egia.mqi.job.JobStatus.RUNNING;

@Service
public class JobService {
    private Job job;
    private JobRepo jobRepo;
    private JobMeasureRepo jobMeasureRepo;

    JobService(JobRepo jobRepo, JobMeasureRepo jobMeasureRepo) {
        this.jobRepo = jobRepo;
        this.jobMeasureRepo = jobMeasureRepo;
    }

    Job addMeasuresToJob(List<Long> measureIds) {
        job = jobRepo.save(
                Job.builder().jobStatus(RUNNING).startTime(new Date()).build());
        for (Long measureId : measureIds) {
            jobMeasureRepo.save(
                    JobMeasure.builder().jobId(job.getJobId()).measureId(measureId).build()
            );
        }

        return job;
    }

    public void fail() {
        job.setJobStatus(FAILURE);
        jobRepo.save(job);
    }
}
