package io.egia.mqi.job;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JobServiceTest {

    @Mock
    JobRepo jobRepo;

    @Mock
    JobMeasureRepo jobMeasureRepo;

    private ArgumentCaptor<Job> jobCaptor = ArgumentCaptor.forClass(Job.class);
    private ArgumentCaptor<JobMeasure> jobMeasureCaptor = ArgumentCaptor.forClass(JobMeasure.class);

    @Test
    public void adds_measures_to_job() {
        when(jobRepo.save(any())).thenReturn(Job.builder().jobId(1L).build());
        JobService jobService = new JobService(jobRepo, jobMeasureRepo);
        jobService.addMeasuresToJob(Arrays.asList(1L, 2L));
        verify(jobRepo).save(jobCaptor.capture());
        verify(jobMeasureRepo, times(2)).save(jobMeasureCaptor.capture());
        assertThat(jobCaptor.getValue().getJobStatus()).isEqualTo(JobStatus.RUNNING);
        assertThat(jobMeasureCaptor.getAllValues().get(0).getJobId()).isEqualTo(1L);
        assertThat(jobMeasureCaptor.getAllValues().get(0).getMeasureId()).isEqualTo(1L);
        assertThat(jobMeasureCaptor.getAllValues().get(1).getJobId()).isEqualTo(1L);
        assertThat(jobMeasureCaptor.getAllValues().get(1).getMeasureId()).isEqualTo(2L);
    }
}