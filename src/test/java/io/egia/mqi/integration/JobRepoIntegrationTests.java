package io.egia.mqi.integration;

import io.egia.mqi.job.Job;
import io.egia.mqi.job.JobRepo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.ZonedDateTime;
import java.util.Collections;

import static io.egia.mqi.helpers.Helpers.UUID1;
import static io.egia.mqi.job.JobStatus.RUNNING;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class JobRepoIntegrationTests {

    @Autowired private JobRepo jobRepo;

    @Before
    public void setUp() {
        for(int i = 1; i < 5; i++) {
            Job job = new Job();
            job.setJobStatus(RUNNING);
            job.setMeasureIds(Collections.singletonList(UUID1));
            job.setLastUpdated(ZonedDateTime.now());
            jobRepo.saveAndFlush(job);
        }
    }

    @Test
    public void jobRepo_updateJobStatus() {
        jobRepo.updateJobStatus(1L, RUNNING);
        assertThat(jobRepo.findById(1L).get().getJobStatus()).isEqualTo(RUNNING);
    }

    @Test
    public void jobRepo_findByMeasureIdsOrderByLastUpdatedDesc() {
        Job job = jobRepo.findFirstByMeasureIdsOrderByLastUpdatedDesc(UUID1).get();
        assertThat(job.getId()).isEqualTo(4);
    }
}
