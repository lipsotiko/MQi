package io.egia.mqi.measure;

import io.egia.mqi.chunk.Chunk;
import io.egia.mqi.chunk.ChunkRepo;
import io.egia.mqi.chunk.ChunkService;
import io.egia.mqi.chunk.ChunkStatus;
import io.egia.mqi.job.Job;
import io.egia.mqi.job.JobRepo;
import io.egia.mqi.job.JobStatus;
import io.egia.mqi.patient.Patient;
import io.egia.mqi.patient.PatientRepo;
import io.egia.mqi.server.Server;
import io.egia.mqi.server.SystemType;
import io.egia.mqi.visit.Visit;
import io.egia.mqi.visit.VisitRepo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MeasureServiceTest {
    @Mock private JobRepo jobRepo;
    @Mock private ChunkRepo chunkRepo;
    @Mock private ChunkService chunkService;
    @Mock private PatientRepo patientRepo;
    @Mock private VisitRepo visitRepo;
    private MeasureProcessorSpy measureProcessor;
    private MeasureService measureService;

    private Server server;
    private Job job;
    private List<Measure> measures;
    private Chunk firstChunkPending;
    private Chunk secondChunkPending;
    private Chunk thirdChunkPending;
    private Chunk firstChunkDone;
    private Chunk secondChunkDone;
    private Chunk thirdChunkDone;

    @Before
    public void setUp() {
        measureProcessor = new MeasureProcessorSpy();
        measureService = new MeasureService(
                chunkRepo
                , chunkService
                , jobRepo
                , patientRepo
                , visitRepo
                , measureProcessor
        );

        measures = new ArrayList<>();
        Measure measure = new Measure();
        measure.setMeasureName("Fake Measure");
        measures.add(measure);

        server = Server.builder().serverId(11L).systemType(SystemType.PRIMARY).build();

        job = new Job();
        job.setJobId(44L);
        job.setJobStatus(JobStatus.RUNNING);

        Chunk c = new Chunk();
        c.setPatientId(99L);
        Patient p = new Patient();
        p.setPatientId(99L);
        List<Patient> patients = new ArrayList<>(Collections.singletonList(p));
        Mockito.when(patientRepo.findByServerIdAndChunkGroup(11L, 1)).thenReturn(patients);

        Visit v = new Visit();
        v.setPatientId(99L);
        List<Visit> visits = new ArrayList<>(Collections.singletonList(v));
        visits.add(v);
        Mockito.when(visitRepo.findByServerIdAndChunkGroup(11L, 1)).thenReturn(visits);

        firstChunkPending = Chunk.builder().serverId(11L).patientId(99L).chunkGroup(1).chunkStatus(ChunkStatus.PENDING).build();
        secondChunkPending = Chunk.builder().serverId(11L).patientId(88L).chunkGroup(2).chunkStatus(ChunkStatus.PENDING).build();
        thirdChunkPending = Chunk.builder().serverId(11L).patientId(77L).chunkGroup(3).chunkStatus(ChunkStatus.PENDING).build();

        Mockito.when(chunkRepo.findTop1ByServerIdAndChunkStatus(11L, ChunkStatus.PENDING))
                .thenReturn(Optional.of(firstChunkPending))
                .thenReturn(Optional.of(secondChunkPending))
                .thenReturn(Optional.of(thirdChunkPending))
                .thenReturn(Optional.empty());
    }

    @Test
    public void verifyMethodsWereCalled() {
        measureService.process(server, job, measures);

        verify(chunkService,times(1)).chunkData();
        verify(chunkRepo,times(4)).findTop1ByServerIdAndChunkStatus(11L, ChunkStatus.PENDING);

        assertThat(measureProcessor.setMeasuresWasCalledWith.get(0).getMeasureName()).isEqualTo("Fake Measure");
        assertThat(measureProcessor.setPatientDataWasCalledWithPatients.get(0).getPatientId()).isEqualTo(99L);
        assertThat(measureProcessor.setPatientDataWasCalledWithVisits.get(0).getPatientId()).isEqualTo(99L);

        verify(chunkRepo,times(1))
                .updateChunkStatusByServerIdAndChunkGroup(11L,1, ChunkStatus.DONE);
        verify(chunkRepo,times(1))
                .updateChunkStatusByServerIdAndChunkGroup(11L,2, ChunkStatus.DONE);
        verify(chunkRepo,times(1))
                .updateChunkStatusByServerIdAndChunkGroup(11L,3, ChunkStatus.DONE);

        assertThat(measureProcessor.processWasCalled).isEqualTo(true);
        assertThat(measureProcessor.clearWasCalled).isEqualTo(true);

        verify(jobRepo, times(1)).updateJobStatus(job.getJobId(), JobStatus.SUCCESS);
    }

    @Test
    public void verifyNothingIsProcessedWhenNoMeasuresAreSupplied() {
        List<Measure> measures = Collections.emptyList();
        measureService.process(server, job, measures);
        verify(chunkService,times(0)).chunkData();
        assertThat(measureProcessor.processWasCalled).isEqualTo(false);
    }

}
