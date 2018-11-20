package io.egia.mqi.job;

import io.egia.mqi.chunk.ChunkService;
import io.egia.mqi.measure.Measure;
import io.egia.mqi.measure.MeasureRepo;
import io.egia.mqi.measure.MeasureService;
import io.egia.mqi.server.Server;
import io.egia.mqi.server.ServerService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

import static io.egia.mqi.server.SystemType.PRIMARY;
import static io.egia.mqi.server.SystemType.SECONDARY;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class JobControllerTest {

    @Mock private JobService jobService;
    @Mock private MeasureRepo measureRepo;
    @Mock private ChunkService chunkService;
    @Mock private MeasureService measureService;
    @Mock private ServerService serverService;
    @Mock private JobProgressMonitor jobProgressMonitor;
    private JobController jobController;
    private List<Long> stubbedMeasureIds = Arrays.asList(1L, 2L);
    private Job job = Job.builder().jobId(1L).build();
    private Server primaryServer = Server.builder().systemType(PRIMARY).build();
    private Server secondaryServer = Server.builder().systemType(SECONDARY).build();
    private List<Measure> measures;

    @Before
    public void setUp() {
        jobController = new JobController(
                jobService,
                measureRepo,
                chunkService,
                measureService,
                serverService,
                jobProgressMonitor);

        when(jobService.addMeasuresToJob(stubbedMeasureIds)).thenReturn(job);
        Measure measureA = new Measure();
        measureA.setMeasureId(1L);
        Measure measureB = new Measure();
        measureB.setMeasureId(2L);

        measures = Arrays.asList(measureA, measureB);
        when(measureRepo.findAllById(stubbedMeasureIds)).thenReturn(measures);
    }

    @Test
    public void primary_server_adds_measures_to_job_service() throws UnknownHostException {
        when(serverService.getServerFromHostNameAndPort(any())).thenReturn(primaryServer);
        jobController.process(stubbedMeasureIds);
        verify(jobService, times(1)).addMeasuresToJob(stubbedMeasureIds);
    }

    @Test
    public void primary_server_starts_chunking_process() throws UnknownHostException {
        when(serverService.getServerFromHostNameAndPort(any())).thenReturn(primaryServer);
        jobController.process(stubbedMeasureIds);
        verify(chunkService).chunkData(job);
    }

    @Test
    public void primary_server_starts_job_progress_monitor() throws UnknownHostException {
        when(serverService.getServerFromHostNameAndPort(any())).thenReturn(primaryServer);
        jobController.process(stubbedMeasureIds);
        verify(jobProgressMonitor).startMonitoringJob(60000, job.getJobId());
    }

    @Test
    public void primary_server_starts_measure_service_process() throws UnknownHostException {
        when(serverService.getServerFromHostNameAndPort(any())).thenReturn(primaryServer);
        jobController.process(stubbedMeasureIds);
        verify(measureService).process(primaryServer, measures);
    }

    @Test
    public void secondary_server_does_nothing_yet() throws UnknownHostException {
        when(serverService.getServerFromHostNameAndPort(any())).thenReturn(secondaryServer);
        jobController.process(stubbedMeasureIds);
        verify(jobService, never()).addMeasuresToJob(stubbedMeasureIds);
        verify(chunkService, never()).chunkData(job);
        verify(jobProgressMonitor, never()).startMonitoringJob(5000, job.getJobId());
        verify(measureService, never()).process(secondaryServer, measures);
    }
}
