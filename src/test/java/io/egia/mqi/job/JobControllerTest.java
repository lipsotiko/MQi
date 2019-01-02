package io.egia.mqi.job;

import io.egia.mqi.chunk.ChunkService;
import io.egia.mqi.measure.Measure;
import io.egia.mqi.measure.MeasureRepo;
import io.egia.mqi.measure.MeasureService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static io.egia.mqi.helpers.Helpers.UUID1;
import static io.egia.mqi.helpers.Helpers.UUID2;
import static io.egia.mqi.job.JobStatus.DONE;
import static io.egia.mqi.job.JobStatus.RUNNING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class JobControllerTest {

    @Mock
    private JobRepo jobRepo;
    @Mock
    private MeasureRepo measureRepo;
    @Mock
    private ChunkService chunkService;
    @Mock
    private MeasureService measureService;
    @Mock
    private JobProgressMonitor jobProgressMonitor;
    private JobController jobController;
    private List<UUID> stubbedMeasureIds = Arrays.asList(UUID1, UUID2);
    private Job job = Job.builder().id(1L).build();
    private List<Measure> measures;

    @Captor
    private ArgumentCaptor<Job> jobArgumentCaptor = ArgumentCaptor.forClass(Job.class);

    @Before
    public void setUp() {
        jobController = new JobController(
                jobRepo,
                measureRepo,
                chunkService,
                measureService,
                jobProgressMonitor);

        Measure measureA = new Measure();
        measureA.setMeasureId(UUID1);
        Measure measureB = new Measure();
        measureB.setMeasureId(UUID2);

        measures = Arrays.asList(measureA, measureB);

        when(jobRepo.saveAndFlush(any())).thenReturn(job);

        when(measureRepo.findAllById(stubbedMeasureIds)).thenReturn(measures);
    }

    @Test
    public void adds_measures_to_job_service() {
        jobController.process(stubbedMeasureIds);
        verify(jobRepo, times(2)).saveAndFlush(jobArgumentCaptor.capture());
        assertThat(jobArgumentCaptor.getAllValues().get(0).getMeasureIds()).isEqualTo(stubbedMeasureIds);
        assertThat(jobArgumentCaptor.getAllValues().get(0).getJobStatus()).isEqualTo(RUNNING);
        assertThat(jobArgumentCaptor.getAllValues().get(1).getJobStatus()).isEqualTo(DONE);
    }

    @Test
    public void starts_chunking_process() {
        jobController.process(stubbedMeasureIds);
        verify(chunkService).chunkData(job);
    }

    @Test
    public void starts_job_progress_monitor() {
        jobController.process(stubbedMeasureIds);
        verify(jobProgressMonitor).startMonitoringJob(1000, job.getId());
    }

    @Test
    public void starts_measure_service_process() throws Exception {
        jobController.process(stubbedMeasureIds);
        verify(measureService).process(measures, 1L);
    }

}
