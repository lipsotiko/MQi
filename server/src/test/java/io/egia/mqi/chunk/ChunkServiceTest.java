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
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ChunkServiceTest {

    @Mock
    private ServerRepository serverRepository;
    @Mock
    private PatientRecordCountRepository patientRecordCountRepository;
    @Mock
    private ChunkRepository chunkRepository;
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

        servers.add(Server.builder().serverId(11L).build());
        servers.add(Server.builder().serverId(22L).build());
        servers.add(Server.builder().serverId(33L).build());

        when(serverRepository.findAll()).thenReturn(servers);
        when(patientRecordCountRepository.findTop5000By())
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

        //Expected results
        expected_1.add(buildChunk(1L, 11L, 100, 1));
        expected_1.add(buildChunk(2L, 22L, 200, 1));
        expected_1.add(buildChunk(3L, 33L, 300, 1));
        expected_1.add(buildChunk(4L, 11L, 100, 1));
        expected_1.add(buildChunk(5L, 22L, 200, 1));
        expected_1.add(buildChunk(6L, 33L, 300, 1));
        expected_1.add(buildChunk(7L, 11L, 100, 1));
        expected_1.add(buildChunk(8L, 22L, 200, 1));
        expected_1.add(buildChunk(9L, 33L, 300, 1));

        expected_2.add(buildChunk(10L, 11L, 100, 2));
        expected_2.add(buildChunk(11L, 22L, 200, 2));
        expected_2.add(buildChunk(12L, 33L, 300, 2));
        expected_2.add(buildChunk(13L, 11L, 100, 2));
        expected_2.add(buildChunk(14L, 22L, 200, 2));
        expected_2.add(buildChunk(15L, 33L, 300, 2));
        expected_2.add(buildChunk(16L, 11L, 100, 2));
        expected_2.add(buildChunk(17L, 22L, 200, 2));
        expected_2.add(buildChunk(18L, 33L, 300, 2));
    }

    @Test
    public void chunkDataTest() {
        ChunkService chunkService =
                new ChunkService(serverRepository,
                        chunkRepository,
                        patientRecordCountRepository);
        chunkService.chunkData(measures);
        verify(chunkRepository, times(2)).saveAll(captor.capture());
        assertThat(captor.getAllValues().get(0)).isEqualTo(expected_1);
        assertThat(captor.getAllValues().get(1)).isEqualTo(expected_2);
    }

    private Chunk buildChunk(long l, long l2, long l3, int l4) {
        return Chunk.builder().patientId(l).serverId(l2).recordCount(l3).chunkGroup(l4).chunkStatus(ChunkStatus.PENDING).build();
    }

    private PatientRecordCount buildPatientRecordCount(long l, long l2) {
        return PatientRecordCount.builder().patientId(l).recordCount(l2).lastUpdated(ZonedDateTime.now()).build();
    }

}
