package io.egia.mqi.chunk;

import io.egia.mqi.job.Job;
import io.egia.mqi.job.JobRepo;
import io.egia.mqi.measure.Measure;
import io.egia.mqi.patient.PatientRecordCount;
import io.egia.mqi.patient.PatientRecordCountRepo;
import io.egia.mqi.server.Server;
import io.egia.mqi.server.ServerRepo;
import io.egia.mqi.server.SystemType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static io.egia.mqi.chunk.ChunkStatus.PENDING;
import static io.egia.mqi.server.SystemType.PRIMARY;
import static io.egia.mqi.server.SystemType.SECONDARY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ChunkServiceTest {

    @Mock
    private ServerRepo serverRepo;
    @Mock
    private PatientRecordCountRepo patientRecordCountRepo;
    @Mock
    private ChunkRepo chunkRepo;
    @Mock
    private JobRepo jobRepo;
    @Captor
    private ArgumentCaptor<List<Chunk>> chunkCaptor = ArgumentCaptor.forClass(List.class);
    @Captor
    private ArgumentCaptor<Job> jobCaptor = ArgumentCaptor.forClass(Job.class);

    private List<PatientRecordCount> firstPatientRecordCounts = new ArrayList<>();
    private List<PatientRecordCount> secondPatientRecordCounts = new ArrayList<>();

    private List<Server> servers = new ArrayList<>();
    private List<Chunk> expected_1 = new ArrayList<>();
    private List<Chunk> expected_2 = new ArrayList<>();

    private Job job;

    @Before
    public void setUp() {
        when(patientRecordCountRepo.count()).thenReturn(18L);

        firstPatientRecordCounts.add(buildPatientRecordCount(1L, 100));
        firstPatientRecordCounts.add(buildPatientRecordCount(2L, 200));
        firstPatientRecordCounts.add(buildPatientRecordCount(3L, 300));
        firstPatientRecordCounts.add(buildPatientRecordCount(4L, 100));
        firstPatientRecordCounts.add(buildPatientRecordCount(5L, 200));
        firstPatientRecordCounts.add(buildPatientRecordCount(6L, 300));
        firstPatientRecordCounts.add(buildPatientRecordCount(7L, 100));
        firstPatientRecordCounts.add(buildPatientRecordCount(8L, 200));
        firstPatientRecordCounts.add(buildPatientRecordCount(9L, 300));

        secondPatientRecordCounts.add(buildPatientRecordCount(10L, 100));
        secondPatientRecordCounts.add(buildPatientRecordCount(11L, 200));
        secondPatientRecordCounts.add(buildPatientRecordCount(12L, 300));
        secondPatientRecordCounts.add(buildPatientRecordCount(13L, 100));
        secondPatientRecordCounts.add(buildPatientRecordCount(14L, 200));
        secondPatientRecordCounts.add(buildPatientRecordCount(15L, 300));
        secondPatientRecordCounts.add(buildPatientRecordCount(16L, 100));
        secondPatientRecordCounts.add(buildPatientRecordCount(17L, 200));
        secondPatientRecordCounts.add(buildPatientRecordCount(18L, 300));

        servers.add(Server.builder().serverId(11L).systemType(PRIMARY).pageSize(9).build());
        servers.add(Server.builder().serverId(22L).systemType(SECONDARY).build());
        servers.add(Server.builder().serverId(33L).systemType(SECONDARY).build());

        when(serverRepo.findAll()).thenReturn(servers);
        when(patientRecordCountRepo.findBy(any()))
                .thenReturn(firstPatientRecordCounts)
                .thenReturn(secondPatientRecordCounts)
                .thenReturn(Collections.emptyList());

        Measure measureUpdatedYesterday = new Measure();
        measureUpdatedYesterday.setMeasureId(111L);
        measureUpdatedYesterday.setLastUpdated(ZonedDateTime.now().minusDays(1));

        Measure measureUpdatedToday = new Measure();
        measureUpdatedToday.setMeasureId(222L);
        measureUpdatedToday.setLastUpdated(ZonedDateTime.now());

        //Expected results
        expected_1.add(buildChunk(1L, 11L, 100, 0));
        expected_1.add(buildChunk(2L, 22L, 200, 0));
        expected_1.add(buildChunk(3L, 33L, 300, 0));
        expected_1.add(buildChunk(4L, 11L, 100, 0));
        expected_1.add(buildChunk(5L, 22L, 200, 0));
        expected_1.add(buildChunk(6L, 33L, 300, 0));
        expected_1.add(buildChunk(7L, 11L, 100, 0));
        expected_1.add(buildChunk(8L, 22L, 200, 0));
        expected_1.add(buildChunk(9L, 33L, 300, 0));

        expected_2.add(buildChunk(10L, 11L, 100, 1));
        expected_2.add(buildChunk(11L, 22L, 200, 1));
        expected_2.add(buildChunk(12L, 33L, 300, 1));
        expected_2.add(buildChunk(13L, 11L, 100, 1));
        expected_2.add(buildChunk(14L, 22L, 200, 1));
        expected_2.add(buildChunk(15L, 33L, 300, 1));
        expected_2.add(buildChunk(16L, 11L, 100, 1));
        expected_2.add(buildChunk(17L, 22L, 200, 1));
        expected_2.add(buildChunk(18L, 33L, 300, 1));

        job = Job.builder().build();
    }

    @Test
    public void data_is_chunked() {
        ChunkService chunkService =
                new ChunkService(serverRepo, chunkRepo, jobRepo, patientRecordCountRepo);

        chunkService.chunkData(job);
        verify(chunkRepo, times(2)).saveAll(chunkCaptor.capture());
        assertThat(chunkCaptor.getAllValues().get(0)).isEqualTo(expected_1);
        assertThat(chunkCaptor.getAllValues().get(1)).isEqualTo(expected_2);
    }

    @Test
    public void initial_patient_count_is_saved_to_job() {
        ChunkService chunkService =
                new ChunkService(serverRepo, chunkRepo, jobRepo, patientRecordCountRepo);
        chunkService.chunkData(job);
        verify(jobRepo).save(jobCaptor.capture());
        assertThat(jobCaptor.getAllValues().get(0).getInitialPatientCount()).isEqualTo(18);
    }

    @Test
    public void page_size_is_rounded_up() {
        List<Server> servers = Collections.singletonList(
                Server.builder().systemType(PRIMARY).pageSize(5000).build());
        when(serverRepo.findAll()).thenReturn(servers);
        when(patientRecordCountRepo.count()).thenReturn(50L);
        ChunkService chunkService =
                new ChunkService(serverRepo, chunkRepo, jobRepo, patientRecordCountRepo);
        chunkService.chunkData(Job.builder().build());
        verify(patientRecordCountRepo,times(1)).findBy(any());
    }

    private Chunk buildChunk(long l, long l2, long l3, int l4) {
        return Chunk.builder().patientId(l).serverId(l2).recordCount(l3).chunkGroup(l4).chunkStatus(PENDING).build();
    }

    private PatientRecordCount buildPatientRecordCount(long l, long l2) {
        return PatientRecordCount.builder().patientId(l).recordCount(l2).lastUpdated(ZonedDateTime.now()).build();
    }

}
