package io.egia.mqi.integration;

import io.egia.mqi.StandaloneConfig;
import io.egia.mqi.job.Job;
import io.egia.mqi.job.JobRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {StandaloneConfig.class})
public class JobRepositoryIntegrationTests {

    @Autowired
    private JobRepository jobRepository;

    @Before
    public void setUp() {
        for(int i = 5; i > 0; i--) {
            Job job = new Job();
            String job1Name = String.format("jUnit Test Job %s", i);
            job.setJobName(job1Name);
            job.setProcessType("measure");
            job.setStatus("pending");
            job.setOrderId(i);
            jobRepository.save(job);
        }
    }

    @Test
    public void findByJobName() {
        assertThat(jobRepository.findByJobName("jUnit Test Job 1")).isNotNull();
    }

    @Test
    public void findByStatusOrderByOrderIdAsc() {
        List<Job> jobs = jobRepository.findByStatusOrderByOrderIdAsc("pending");
        Job job = jobs.get(0);
        assertThat(job.getOrderId()).isEqualTo(1L);
        assertThat(job.getJobId()).isEqualTo(5L);
    }

    @After
    public void tearDown() {
        List<Job> jobs = jobRepository.findAll();
        for(Job j: jobs) {
            jobRepository.delete(j);
        }
    }

}
