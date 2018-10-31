package io.egia.mqi.job;

import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class JobPublisherTest {

    @Autowired private JobRepo jobRepo;
    @Autowired private JobPublisher jobPublisher;

    @Test
    public void publishes_job_updates() throws InterruptedException {
        TestSubscriber<Job> observer = new TestSubscriber<>();
        Flowable<Job> publisher = jobPublisher.getPublisher();
        publisher.subscribe(observer);
        jobRepo.save(Job.builder().jobStatus(JobStatus.RUNNING).build());
        Thread.sleep(1000);
        observer.assertSubscribed();
        assertThat(observer.values().get(0).getJobStatus()).isEqualTo(JobStatus.RUNNING);
    }
}