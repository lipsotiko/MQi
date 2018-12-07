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
    private List<Long> stubbedMeasureIds = Arrays.asList(1L, 2L);
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
        measureA.setMeasureId(1L);
        Measure measureB = new Measure();
        measureB.setMeasureId(2L);

        measures = Arrays.asList(measureA, measureB);

        when(jobRepo.save(any())).thenReturn(Job.builder().id(1L).build());

        when(measureRepo.findAllById(stubbedMeasureIds)).thenReturn(measures);
    }

    @Test
    public void adds_measures_to_job_service() {
        jobController.process(stubbedMeasureIds);
        verify(jobRepo, times(1)).save(jobArgumentCaptor.capture());
        assertThat(jobArgumentCaptor.getValue().getMeasureIds()).isEqualTo(stubbedMeasureIds);
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
    public void starts_measure_service_process() {
        jobController.process(stubbedMeasureIds);
        verify(measureService).process(measures, 1L);
    }

}
