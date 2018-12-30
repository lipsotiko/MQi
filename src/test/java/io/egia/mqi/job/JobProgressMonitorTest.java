package io.egia.mqi.job;

import io.egia.mqi.chunk.ChunkRepo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Optional;

import static io.egia.mqi.chunk.ChunkStatus.PROCESSED;
import static io.egia.mqi.helpers.Helpers.job;
import static io.egia.mqi.job.JobStatus.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class JobProgressMonitorTest {

    @Mock
    private ChunkRepo chunkRepo;
    @Mock
    private JobRepo jobRepo;
    @Mock
    private SimpMessagingTemplate template;
    @Captor
    private ArgumentCaptor<Job> messageCaptor = ArgumentCaptor.forClass(Job.class);

    @Before
    public void setUp() {
        when(jobRepo.findById(1L))
                .thenReturn(job(1L, 30L, RUNNING))
                .thenReturn(job(1L, 30L, RUNNING))
                .thenReturn(job(1L, 30L, RUNNING))
                .thenReturn(job(1L, 30L, FAILURE))
                .thenReturn(Optional.empty());
    }

    @Test
    public void updates_job_with_processed_patients_count() {
        when(chunkRepo.countByChunkStatus(PROCESSED)).thenReturn(10L).thenReturn(20L).thenReturn(30L);
        JobProgressMonitor jobProgressMonitor = new JobProgressMonitor(jobRepo, chunkRepo, template);
        jobProgressMonitor.startMonitoringJob(1, 1L);

        verify(template, times(3)).convertAndSend(eq("/topic/job"), messageCaptor.capture());
        assertThat(messageCaptor.getAllValues().get(0).getProcessedPatientCount()).isEqualTo(10L);
        assertThat(messageCaptor.getAllValues().get(1).getProcessedPatientCount()).isEqualTo(20L);
        assertThat(messageCaptor.getAllValues().get(2).getProcessedPatientCount()).isEqualTo(30L);
    }

}
