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
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
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

    @Mock private MeasureProcessor measureProcessor;

    private MeasureService measureService;

    private static final String J_UNIT_MEASURE_SERVICE_TEST = "jUnit measure Service Test";

    @Value("${server.port}") private String serverPort;

    @Before
    public void setUp() throws IOException {

        measureService = new MeasureService(
                measureRepository
                , chunkRepository
                , jobRepository
                , serverService
                , patientRepository
                , visitRepository
                , measureProcessor
        );

        Job job = new Job();
        job.setJobId(1L);
        job.setJobName(J_UNIT_MEASURE_SERVICE_TEST);
        job.setProcessType("measure");
        job.setStatus("pending");

        List<Job> jobs = new ArrayList<>();
        jobs.add(job);

        Mockito.when(jobRepository.findByStatusOrderByOrderIdAsc(anyString())).thenReturn(jobs);

        Chunk c = new Chunk();
        c.setPatientId(1L);
        c.setChunkId(1L);

        Patient p = new Patient();
        p.setChunk(c);
        p.setPatientId(1L);

        List<Patient> patients = new ArrayList<>();
        patients.add(p);

        Mockito.when(patientRepository.findByServerIdAndChunkId(anyLong(), anyLong())).thenReturn(patients);

        Visit v = new Visit();
        v.setPatientId(1L);

        List<Visit> visits = new ArrayList<>();
        visits.add(v);

        Mockito.when(visitRepository.findByServerIdAndChunkId(anyLong(), anyLong())).thenReturn(visits);

        Server server = new Server();
        server.setServerId(1L);

        Mockito.when(serverService.getServerFromHostNameAndPort(serverPort)).thenReturn(server);

        List<Chunk> chunks = new ArrayList<>();
        chunks.add(c);

        Mockito.when(chunkRepository.findByServerIdOrderByChunkIdAsc(anyLong())).thenReturn(chunks);
    }

    @Test
    public void verifyMethodsWereCalled() throws UnknownHostException {
        measureService.process();
        verify(jobRepository, times(1)).findByStatusOrderByOrderIdAsc(anyString());
        verify(patientRepository, times(1)).findByServerIdAndChunkId(anyLong(),anyLong());
        verify(visitRepository, times(1)).findByServerIdAndChunkId(anyLong(),anyLong());
        verify(serverService, times(1)).getServerFromHostNameAndPort(serverPort);
        verify(chunkRepository,times(1)).findByServerIdOrderByChunkIdAsc(anyLong());
        verify(measureProcessor,times(1)).iterateOverPatientsAndMeasures();
    }

}
