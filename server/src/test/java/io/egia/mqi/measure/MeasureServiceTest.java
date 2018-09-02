package io.egia.mqi.measure;

import io.egia.mqi.chunk.Chunk;
import io.egia.mqi.chunk.ChunkRepository;
import io.egia.mqi.job.Job;
import io.egia.mqi.job.JobRepository;
import io.egia.mqi.patient.Patient;
import io.egia.mqi.patient.PatientRepository;
import io.egia.mqi.server.Server;
import io.egia.mqi.server.ServerService;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MeasureServiceTest {
    @Mock private JobRepository jobRepository;
    @Mock private MeasureRepository measureRepository;
    @Mock private ChunkRepository chunkRepository;
    @Mock private PatientRepository patientRepository;
    @Mock private VisitRepository visitRepository;
    @Mock private ServerService serverService;
    private MeasureProcessorSpy measureProcessor;
    private MeasureService measureService;
    private static final String J_UNIT_MEASURE_SERVICE_TEST = "jUnit measure Service Test";
    @Value("${server.port}") private String serverPort;

    @Before
    public void setUp() throws IOException {
        measureProcessor = new MeasureProcessorSpy();
        measureService = new MeasureService(
                measureRepository
                , chunkRepository
                , jobRepository
                , serverService
                , patientRepository
                , visitRepository
                , measureProcessor
        );

        List<Measure> measures = new ArrayList<>();
        Measure measure = new Measure();
        measure.setMeasureName("Fake Measure");
        measures.add(measure);
        Mockito.when(measureRepository.findAllByJobId(44L)).thenReturn(measures);

        Job job = new Job();
        job.setJobId(44L);
        job.setJobName(J_UNIT_MEASURE_SERVICE_TEST);
        job.setProcessType("measure");
        job.setStatus("pending");
        List<Job> jobs = new ArrayList<>(Collections.singletonList(job));
        Mockito.when(jobRepository.findByStatusOrderByOrderIdAsc("pending")).thenReturn(jobs);

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
        Mockito.when(visitRepository.findByServerIdAndChunkId(11L, 22L)).thenReturn(visits);

        Server server = Server.builder().serverId(11L).build();
        Mockito.when(serverService.getServerFromHostNameAndPort(serverPort)).thenReturn(server);

        List<Chunk> chunks = new ArrayList<>();
        c.setChunkId(22L);
        chunks.add(c);
        Mockito.when(chunkRepository.findByServerIdOrderByChunkIdAsc(11L)).thenReturn(chunks);
    }

    @Test
    public void verifyMethodsWereCalled() throws UnknownHostException {
        measureService.process();
        verify(jobRepository, times(1)).findByStatusOrderByOrderIdAsc("pending");
        verify(patientRepository, times(1)).findByServerIdAndChunkId(11L,22L);
        verify(visitRepository, times(1)).findByServerIdAndChunkId(11L,22L);
        verify(serverService, times(1)).getServerFromHostNameAndPort(serverPort);
        verify(chunkRepository,times(1)).findByServerIdOrderByChunkIdAsc(11L);
        assertThat(measureProcessor.getSetMeasuresWasCalledWith().get(0).getMeasureName()).isEqualTo("Fake Measure");
        assertThat(measureProcessor.getSetPatientDataWasCalledWithPatients().get(0).getPatientId()).isEqualTo(99L);
        assertThat(measureProcessor.getSetPatientDataWasCalledWithVisits().get(0).getPatientId()).isEqualTo(99L);
    }

}
