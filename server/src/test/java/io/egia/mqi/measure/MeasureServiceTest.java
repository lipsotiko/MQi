package io.egia.mqi.measure;

import io.egia.mqi.chunk.Chunk;
import io.egia.mqi.chunk.ChunkRepository;
import io.egia.mqi.chunk.ChunkService;
import io.egia.mqi.chunk.ChunkStatus;
import io.egia.mqi.job.Job;
import io.egia.mqi.job.JobRepository;
import io.egia.mqi.patient.Patient;
import io.egia.mqi.patient.PatientRepository;
import io.egia.mqi.server.Server;
import io.egia.mqi.server.ServerService;
import io.egia.mqi.job.JobStatus;
import io.egia.mqi.visit.Visit;
import io.egia.mqi.visit.VisitRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MeasureServiceTest {
    @Mock private JobRepository jobRepository;
    @Mock private ChunkRepository chunkRepository;
    @Mock private ChunkService chunkService;
    @Mock private PatientRepository patientRepository;
    @Mock private VisitRepository visitRepository;
    @Mock private ServerService serverService;
    private MeasureProcessorSpy measureProcessor;
    private MeasureService measureService;

    private Server server;
    private Job job;
    private List<Measure> measures;

    @Before
    public void setUp() throws IOException {
        measureProcessor = new MeasureProcessorSpy();
        measureService = new MeasureService(
                chunkRepository
                , chunkService
                , jobRepository
                , patientRepository
                , visitRepository
                , measureProcessor
        );

        measures = new ArrayList<>();
        Measure measure = new Measure();
        measure.setMeasureName("Fake Measure");
        measures.add(measure);

        server = Server.builder().serverId(11L).systemType("primary").build();

        job = new Job();
        job.setJobId(44L);
        job.setJobStatus(JobStatus.RUNNING);

        Chunk c = new Chunk();
        c.setPatientId(99L);
        Patient p = new Patient();
        p.setChunk(c);
        p.setPatientId(99L);
        List<Patient> patients = new ArrayList<>(Collections.singletonList(p));
        Mockito.when(patientRepository.findByServerIdAndChunkId(11L, 22L)).thenReturn(patients);

        Visit v = new Visit();
        v.setPatientId(99L);
        List<Visit> visits = new ArrayList<>(Collections.singletonList(v));
        visits.add(v);
        Mockito.when(visitRepository.findByServerIdAndChunkChunkId(11L, 22L)).thenReturn(visits);

        Chunk firstChunk = Chunk.builder().chunkId(22L).build();
        Chunk secondChunk = Chunk.builder().chunkId(23L).build();
        Chunk thirdChunk = Chunk.builder().chunkId(24L).build();

        Mockito.when(chunkRepository.findFirstByServerIdAndChunkStatus(11L, ChunkStatus.PENDING))
                .thenReturn(Optional.of(firstChunk))
                .thenReturn(Optional.of(secondChunk))
                .thenReturn(Optional.of(thirdChunk))
                .thenReturn(Optional.empty());
    }

    @Test
    public void verifyMethodsWereCalled() throws UnknownHostException {
        measureService.process(server, job, measures);

        verify(chunkService,times(1)).chunkData();
        verify(chunkRepository,times(4)).findFirstByServerIdAndChunkStatus(11L, ChunkStatus.PENDING);

        verify(patientRepository, times(1)).findByServerIdAndChunkId(11L,22L);
        verify(patientRepository, times(1)).findByServerIdAndChunkId(11L,23L);
        verify(patientRepository, times(1)).findByServerIdAndChunkId(11L,24L);

        verify(visitRepository, times(1)).findByServerIdAndChunkChunkId(11L,22L);
        verify(visitRepository, times(1)).findByServerIdAndChunkChunkId(11L,23L);
        verify(visitRepository, times(1)).findByServerIdAndChunkChunkId(11L,24L);

        assertThat(measureProcessor.setMeasuresWasCalledWith.get(0).getMeasureName()).isEqualTo("Fake Measure");
        assertThat(measureProcessor.setPatientDataWasCalledWithPatients.get(0).getPatientId()).isEqualTo(99L);
        assertThat(measureProcessor.setPatientDataWasCalledWithVisits.get(0).getPatientId()).isEqualTo(99L);

        verify(chunkRepository,times(1)).updateChunkStatus(22L, ChunkStatus.DONE);
        verify(chunkRepository,times(1)).updateChunkStatus(23L, ChunkStatus.DONE);
        verify(chunkRepository,times(1)).updateChunkStatus(24L, ChunkStatus.DONE);
        assertThat(measureProcessor.processWasCalled).isEqualTo(true);
        assertThat(measureProcessor.clearWasCalled).isEqualTo(true);

        verify(jobRepository, times(1)).updateJobStatus(job.getJobId(), JobStatus.SUCCESS);
    }

}
