package io.egia.mqi.job;

import io.egia.mqi.chunk.ChunkRepo;
import io.egia.mqi.chunk.ChunkStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JobProgressMonitorTest {

    @Mock
    private ChunkRepo chunkRepo;

    @Mock private JobRepo jobRepo;

    @Captor
    private ArgumentCaptor<Job> jobCaptor = ArgumentCaptor.forClass(Job.class);


    @Test
    public void updates_job_with_processed_patients_count() {
        Optional<Job> pendingJobA = Optional.of(Job.builder().jobId(1L).initialPatientCount(30L).jobStatus(JobStatus.RUNNING).build());
        Optional<Job> pendingJobB = Optional.of(Job.builder().jobId(1L).initialPatientCount(30L).jobStatus(JobStatus.RUNNING).build());
        Optional<Job> pendingJobC = Optional.of(Job.builder().jobId(1L).initialPatientCount(30L).jobStatus(JobStatus.RUNNING).build());

        when(jobRepo.findById(1L))
                .thenReturn(pendingJobA)
                .thenReturn(pendingJobB)
                .thenReturn(pendingJobC)
                .thenReturn(Optional.empty());
        when(chunkRepo.countByChunkStatus(ChunkStatus.DONE))
                .thenReturn(10L)
                .thenReturn(20L)
                .thenReturn(30L);

        JobProgressMonitor jobProgressMonitor = new JobProgressMonitor(jobRepo, chunkRepo);
        jobProgressMonitor.startMonitoringJob(1, 1L);

        verify(jobRepo, times(3)).save(jobCaptor.capture());

        assertThat(jobCaptor.getAllValues().get(0).getProcessedPatientCount()).isEqualTo(10L);
        assertThat(jobCaptor.getAllValues().get(0).getJobStatus()).isEqualTo(JobStatus.RUNNING);

        assertThat(jobCaptor.getAllValues().get(1).getProcessedPatientCount()).isEqualTo(20L);
        assertThat(jobCaptor.getAllValues().get(1).getJobStatus()).isEqualTo(JobStatus.RUNNING);

        assertThat(jobCaptor.getAllValues().get(2).getProcessedPatientCount()).isEqualTo(30L);
        assertThat(jobCaptor.getAllValues().get(2).getJobStatus()).isEqualTo(JobStatus.DONE);
    }

}