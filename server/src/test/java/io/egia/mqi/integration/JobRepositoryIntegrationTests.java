package io.egia.mqi.integration;

import io.egia.mqi.job.Job;
import io.egia.mqi.job.JobRepository;
import io.egia.mqi.job.JobStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class JobRepositoryIntegrationTests {

    @Autowired
    private JobRepository jobRepository;

    @Before
    public void setUp() {
        for(int i = 5; i > 0; i--) {
            Job job = new Job();
            job.setJobStatus(JobStatus.PENDING);
            jobRepository.saveAndFlush(job);
        }
    }

    @Test
    public void findByStatusOrderByJobIdAsc() {
        Optional<List<Job>> jobs = jobRepository.findByJobStatusOrderByJobIdAsc(JobStatus.PENDING);
        assertThat(jobs.get().get(0).getJobId()).isEqualTo(1L);
        assertThat(jobs.get().get(4).getJobId()).isEqualTo(5L);
    }

    @After
    public void tearDown() {
        List<Job> jobs = jobRepository.findAll();
        for(Job j: jobs) {
            jobRepository.delete(j);
        }
    }

}
