package io.egia.mqi.job;

import org.junit.Test;

import static io.egia.mqi.job.JobStatus.DONE;
import static io.egia.mqi.job.JobStatus.RUNNING;
import static org.assertj.core.api.Assertions.assertThat;

public class JobTest {

    @Test
    public void progress_is_at_25_percent() {
        Job job = Job.builder().jobStatus(RUNNING).initialPatientCount(100L).processedPatientCount(25L).build();
        assertThat(job.getProgress()).isEqualTo(25);
    }

    @Test
    public void job_progress_is_at_75_percent() {
        Job job = Job.builder().jobStatus(RUNNING).initialPatientCount(1000L).processedPatientCount(751L).build();
        assertThat(job.getProgress()).isEqualTo(75);
    }

    @Test
    public void progress_is_at_100_percent() {
        Job job = Job.builder().jobStatus(DONE).build();
        assertThat(job.getProgress()).isEqualTo(100);
    }

    @Test
    public void progress_is_at_0_percent() {
        Job job = Job.builder().build();
        assertThat(job.getProgress()).isEqualTo(0);
    }

    @Test
    public void status_is_not_set() {
        Job job = Job.builder().build();
        assertThat(job.getProgress()).isEqualTo(0);
    }
}
