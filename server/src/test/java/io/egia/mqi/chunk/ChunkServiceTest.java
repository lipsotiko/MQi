package io.egia.mqi.chunk;

import io.egia.mqi.measure.Measure;
import io.egia.mqi.patient.PatientRecordCount;
import io.egia.mqi.patient.PatientRecordCountRepository;
import io.egia.mqi.server.Server;
import io.egia.mqi.server.ServerRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ChunkServiceTest {

    @Mock private ServerRepository serverRepository;
    @Mock private PatientRecordCountRepository patientRecordCountRepository;
    @Mock private ChunkRepository chunkRepository;
    @Captor
    private ArgumentCaptor<List<Chunk>> captor = ArgumentCaptor.forClass(List.class);

    private List<PatientRecordCount> firstPatientRecordCounts = new ArrayList<>();
    private List<PatientRecordCount> secondPatientRecordCounts = new ArrayList<>();


    private List<Server> servers = new ArrayList<>();
    private List<Chunk> expected_1 = new ArrayList<>();
    private List<Chunk> expected_2 = new ArrayList<>();
    private List<Measure> measures = new ArrayList<>();

    @Before
    public void setUp() {
        firstPatientRecordCounts.add(PatientRecordCount.builder().patientId(1L).recordCount(100L).build());
        firstPatientRecordCounts.add(PatientRecordCount.builder().patientId(2L).recordCount(200L).build());
        firstPatientRecordCounts.add(PatientRecordCount.builder().patientId(3L).recordCount(300L).build());
        firstPatientRecordCounts.add(PatientRecordCount.builder().patientId(4L).recordCount(100L).build());
        firstPatientRecordCounts.add(PatientRecordCount.builder().patientId(5L).recordCount(200L).build());
        firstPatientRecordCounts.add(PatientRecordCount.builder().patientId(6L).recordCount(300L).build());
        firstPatientRecordCounts.add(PatientRecordCount.builder().patientId(7L).recordCount(100L).build());
        firstPatientRecordCounts.add(PatientRecordCount.builder().patientId(8L).recordCount(200L).build());
        firstPatientRecordCounts.add(PatientRecordCount.builder().patientId(9L).recordCount(300L).build());

        secondPatientRecordCounts.add(PatientRecordCount.builder().patientId(10L).recordCount(100L).build());
        secondPatientRecordCounts.add(PatientRecordCount.builder().patientId(11L).recordCount(200L).build());
        secondPatientRecordCounts.add(PatientRecordCount.builder().patientId(12L).recordCount(300L).build());
        secondPatientRecordCounts.add(PatientRecordCount.builder().patientId(13L).recordCount(100L).build());
        secondPatientRecordCounts.add(PatientRecordCount.builder().patientId(14L).recordCount(200L).build());
        secondPatientRecordCounts.add(PatientRecordCount.builder().patientId(15L).recordCount(300L).build());
        secondPatientRecordCounts.add(PatientRecordCount.builder().patientId(16L).recordCount(100L).build());
        secondPatientRecordCounts.add(PatientRecordCount.builder().patientId(17L).recordCount(200L).build());
        secondPatientRecordCounts.add(PatientRecordCount.builder().patientId(18L).recordCount(300L).build());


        servers.add(Server.builder().serverId(11L).build());
        servers.add(Server.builder().serverId(22L).build());
        servers.add(Server.builder().serverId(33L).build());

        when(serverRepository.findAll()).thenReturn(servers);

        when(patientRecordCountRepository.findAll())
                .thenReturn(firstPatientRecordCounts)
                .thenReturn(secondPatientRecordCounts)
                .thenReturn(Collections.emptyList());

        Measure measureUpdatedYesterday = new Measure();
        measureUpdatedYesterday.setMeasureId(111L);
        measureUpdatedYesterday.setLastUpdated(ZonedDateTime.now().minusDays(1));
        measures.add(measureUpdatedYesterday);

        Measure measureUpdatedToday = new Measure();
        measureUpdatedToday.setMeasureId(222L);
        measureUpdatedToday.setLastUpdated(ZonedDateTime.now());
        measures.add(measureUpdatedToday);

        expected_1.add(Chunk.builder().patientId(1L).serverId(11L).recordCount(100L).chunkStatus(ChunkStatus.PENDING).build());
        expected_1.add(Chunk.builder().patientId(2L).serverId(22L).recordCount(200L).chunkStatus(ChunkStatus.PENDING).build());
        expected_1.add(Chunk.builder().patientId(3L).serverId(33L).recordCount(300L).chunkStatus(ChunkStatus.PENDING).build());
        expected_1.add(Chunk.builder().patientId(4L).serverId(11L).recordCount(100L).chunkStatus(ChunkStatus.PENDING).build());
        expected_1.add(Chunk.builder().patientId(5L).serverId(22L).recordCount(200L).chunkStatus(ChunkStatus.PENDING).build());
        expected_1.add(Chunk.builder().patientId(6L).serverId(33L).recordCount(300L).chunkStatus(ChunkStatus.PENDING).build());
        expected_1.add(Chunk.builder().patientId(7L).serverId(11L).recordCount(100L).chunkStatus(ChunkStatus.PENDING).build());
        expected_1.add(Chunk.builder().patientId(8L).serverId(22L).recordCount(200L).chunkStatus(ChunkStatus.PENDING).build());
        expected_1.add(Chunk.builder().patientId(9L).serverId(33L).recordCount(300L).chunkStatus(ChunkStatus.PENDING).build());

        expected_2.add(Chunk.builder().patientId(10L).serverId(11L).recordCount(100L).chunkStatus(ChunkStatus.PENDING).build());
        expected_2.add(Chunk.builder().patientId(11L).serverId(22L).recordCount(200L).chunkStatus(ChunkStatus.PENDING).build());
        expected_2.add(Chunk.builder().patientId(12L).serverId(33L).recordCount(300L).chunkStatus(ChunkStatus.PENDING).build());
        expected_2.add(Chunk.builder().patientId(13L).serverId(11L).recordCount(100L).chunkStatus(ChunkStatus.PENDING).build());
        expected_2.add(Chunk.builder().patientId(14L).serverId(22L).recordCount(200L).chunkStatus(ChunkStatus.PENDING).build());
        expected_2.add(Chunk.builder().patientId(15L).serverId(33L).recordCount(300L).chunkStatus(ChunkStatus.PENDING).build());
        expected_2.add(Chunk.builder().patientId(16L).serverId(11L).recordCount(100L).chunkStatus(ChunkStatus.PENDING).build());
        expected_2.add(Chunk.builder().patientId(17L).serverId(22L).recordCount(200L).chunkStatus(ChunkStatus.PENDING).build());
        expected_2.add(Chunk.builder().patientId(18L).serverId(33L).recordCount(300L).chunkStatus(ChunkStatus.PENDING).build());
    }

    @Test
    public void chunkDataTest() {
        ChunkService chunkService = new ChunkService(serverRepository, chunkRepository, patientRecordCountRepository);
        chunkService.chunkData(measures);
        verify(chunkRepository, times(2)).saveAll(captor.capture());
        assertThat(captor.getAllValues().get(0)).isEqualTo(expected_1);
        assertThat(captor.getAllValues().get(1)).isEqualTo(expected_2);
    }

}
