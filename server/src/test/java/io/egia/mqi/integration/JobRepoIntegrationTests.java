package io.egia.mqi.integration;

import io.egia.mqi.job.JobRepo;
import io.egia.mqi.job.JobStatus;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class JobRepoIntegrationTests extends AbstractRepositoryTest {

    @Autowired
    private JobRepo jobRepo;

    @Test
    public void jobRepo_updateJobStatus() {
        jobRepo.updateJobStatus(1L, JobStatus.RUNNING);
        assertThat(jobRepo.findById(1L).get().getJobStatus()).isEqualTo(JobStatus.RUNNING);
    }
}
