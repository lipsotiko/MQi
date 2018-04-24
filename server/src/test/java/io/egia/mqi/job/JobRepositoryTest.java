package io.egia.mqi.job;

import io.egia.mqi.StandaloneConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {StandaloneConfig.class})
public class JobRepositoryTest {

    @Autowired
    private JobRepository jobRepository;

    private String jobName = "jUnit Test job";

    @Before
    public void addJob() {
        Job job = new Job();
        job.setJobName(jobName);
        job.setProcessType("measure");
        jobRepository.saveAndFlush(job);
    }

    @Test
    public void findJob() {
        assertThat(jobRepository.findByJobName(jobName)).isNotNull();
    }

    @After
    public void removeJob() {
        Long jobId = jobRepository.findByJobName(jobName).getJobId();
        jobRepository.deleteByJobId(jobId);
    }
}
