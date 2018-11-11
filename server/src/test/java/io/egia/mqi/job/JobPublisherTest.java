package io.egia.mqi.job;

import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static io.egia.mqi.job.JobStatus.RUNNING;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class JobPublisherTest {

    @Autowired private JobRepo jobRepo;
    @Autowired private JobPublisher jobPublisher;
    private TestSubscriber<Job> observer;

    @Before
    public void setUp() {
        observer = new TestSubscriber<>();
        Flowable<Job> publisher = jobPublisher.getPublisher();
        publisher.subscribe(observer);
    }

    @Test
    public void publishes_job_updates() throws InterruptedException {
        jobRepo.save(Job.builder().jobStatus(RUNNING).build());
        Thread.sleep(1000);
        assertThat(observer.values().get(0).getJobStatus()).isEqualTo(RUNNING);
    }
}
