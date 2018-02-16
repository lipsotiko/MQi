package io.egia.mqi.domain;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import io.egia.mqi.StandaloneConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes={StandaloneConfig.class})
public class JobRepositoryTest {
	
	@Autowired
	private JobRepository jobRepository;
	
	private String jobName = "jUnit Test Job";
	
	@Before
	public void addJob() {
		Job job = new Job();
		job.setJobName(jobName);
		job.setProcessType("measure");
		jobRepository.saveAndFlush(job);
	}
	
	@Test
	public void findJob() {
		assertEquals(jobName, jobRepository.findByJobName(jobName).get(0).getJobName());
	}
	
	@After
	public void removeJob() {
		jobRepository.delete(jobRepository.findByJobName(jobName).get(0).getJobId());
	}
	
}
