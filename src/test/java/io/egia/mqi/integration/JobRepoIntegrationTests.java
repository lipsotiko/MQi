package io.egia.mqi.integration;

import io.egia.mqi.job.Job;
import io.egia.mqi.job.JobRepo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Collections;

import static io.egia.mqi.job.JobStatus.RUNNING;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class JobRepoIntegrationTests {

    @Autowired private JobRepo jobRepo;

    @MockBean
    private SimpMessagingTemplate template;

    @Before
    public void setUp() {
        for(int i = 1; i < 5; i++) {
            Job job = new Job();
            job.setJobStatus(RUNNING);
            job.setMeasureIds(Collections.singletonList((long) 1));
            job.setLastUpdated(Date.valueOf(LocalDate.now()));
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
        Job job = jobRepo.findFirstByMeasureIdsOrderByLastUpdatedDesc(1L).get();
        assertThat(job.getId()).isEqualTo(1);
    }
}
