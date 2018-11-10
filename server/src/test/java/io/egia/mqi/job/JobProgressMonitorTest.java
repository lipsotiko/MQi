package io.egia.mqi.job;

import io.egia.mqi.chunk.ChunkRepo;
import io.egia.mqi.chunk.ChunkStatus;
import io.egia.mqi.helpers.Helpers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static io.egia.mqi.helpers.Helpers.job;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JobProgressMonitorTest {

    @Mock
    private ChunkRepo chunkRepo;
    @Mock
    private JobRepo jobRepo;
    @Captor
    private ArgumentCaptor<Job> jobCaptor = ArgumentCaptor.forClass(Job.class);

    @Before
    public void setUp() {
        when(jobRepo.findById(1L))
                .thenReturn(job(1L, 30L, JobStatus.RUNNING))
                .thenReturn(job(1L, 30L, JobStatus.RUNNING))
                .thenReturn(job(1L, 30L, JobStatus.RUNNING))
                .thenReturn(job(1L, 30L, JobStatus.FAILURE))
                .thenReturn(Optional.empty());
    }

    @Test
    public void updates_job_with_processed_patients_count() {
        when(chunkRepo.countByChunkStatus(ChunkStatus.DONE)).thenReturn(10L).thenReturn(20L).thenReturn(30L);
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

    @Test
    public void stops_moitoring_a_job_that_failed() {
        when(chunkRepo.countByChunkStatus(ChunkStatus.DONE)).thenReturn(10L).thenReturn(20L).thenReturn(25L);
        JobProgressMonitor jobProgressMonitor = new JobProgressMonitor(jobRepo, chunkRepo);
        jobProgressMonitor.startMonitoringJob(1, 1L);
        verify(jobRepo,times(3)).save(any());
    }

}