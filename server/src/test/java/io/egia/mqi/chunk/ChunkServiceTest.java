package io.egia.mqi.chunk;

import io.egia.mqi.measure.Measure;
import io.egia.mqi.patient.PatientMeasureLog;
import io.egia.mqi.patient.PatientMeasureLogRepository;
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

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ChunkServiceTest {

    @Mock
    private ServerRepository serverRepository;
    @Mock
    private PatientRecordCountRepository patientRecordCountRepository;
    @Mock
    private ChunkRepository chunkRepository;
    @Mock
    private PatientMeasureLogRepository patientMeasureLogRepository;
    @Captor
    private ArgumentCaptor<List<Chunk>> captor = ArgumentCaptor.forClass(List.class);

    private List<PatientRecordCount> firstPatientRecordCounts = new ArrayList<>();
    private List<PatientRecordCount> secondPatientRecordCounts = new ArrayList<>();
    private List<PatientMeasureLog> patientMeasureLogs = new ArrayList<>();


    private List<Server> servers = new ArrayList<>();
    private List<Chunk> expected_1 = new ArrayList<>();
    private List<Chunk> expected_2 = new ArrayList<>();
    private List<Measure> measures = new ArrayList<>();

    @Before
    public void setUp() {
        firstPatientRecordCounts.add(buildPatientRecordCount(1L, 100L));
        firstPatientRecordCounts.add(buildPatientRecordCount(2L, 200L));
        firstPatientRecordCounts.add(buildPatientRecordCount(3L, 300L));
        firstPatientRecordCounts.add(buildPatientRecordCount(4L, 100L));
        firstPatientRecordCounts.add(buildPatientRecordCount(5L, 200L));
        firstPatientRecordCounts.add(buildPatientRecordCount(6L, 300L));
        firstPatientRecordCounts.add(buildPatientRecordCount(7L, 100L));
        firstPatientRecordCounts.add(buildPatientRecordCount(8L, 200L));
        firstPatientRecordCounts.add(buildPatientRecordCount(9L, 300L));

        secondPatientRecordCounts.add(buildPatientRecordCount(10L, 100L));
        secondPatientRecordCounts.add(buildPatientRecordCount(11L, 200L));
        secondPatientRecordCounts.add(buildPatientRecordCount(12L, 300L));
        secondPatientRecordCounts.add(buildPatientRecordCount(13L, 100L));
        secondPatientRecordCounts.add(buildPatientRecordCount(14L, 200L));
        secondPatientRecordCounts.add(buildPatientRecordCount(15L, 300L));
        secondPatientRecordCounts.add(buildPatientRecordCount(16L, 100L));
        secondPatientRecordCounts.add(buildPatientRecordCount(17L, 200L));
        secondPatientRecordCounts.add(buildPatientRecordCount(18L, 300L));

        //Setup for process to filter patients that don't need to be analysed
        secondPatientRecordCounts.add(buildPatientRecordCount(19L, 300L));
        patientMeasureLogs.add(
                PatientMeasureLog.builder()
                        .patientId(19L).measureId(222L).lastUpdated(ZonedDateTime.now()).build());
        List<Long> patientIds_2 =
                secondPatientRecordCounts.stream().map(PatientRecordCount::getPatientId).collect(Collectors.toList());
        when(patientMeasureLogRepository.findAllById(patientIds_2)).thenReturn(patientMeasureLogs);

        //More setup to mimic multiple queries to the database until there are no more result sets
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

        //Expected results
        expected_1.add(buildChunk(1L, 11L, 100L));
        expected_1.add(buildChunk(2L, 22L, 200L));
        expected_1.add(buildChunk(3L, 33L, 300L));
        expected_1.add(buildChunk(4L, 11L, 100L));
        expected_1.add(buildChunk(5L, 22L, 200L));
        expected_1.add(buildChunk(6L, 33L, 300L));
        expected_1.add(buildChunk(7L, 11L, 100L));
        expected_1.add(buildChunk(8L, 22L, 200L));
        expected_1.add(buildChunk(9L, 33L, 300L));

        expected_2.add(buildChunk(10L, 11L, 100L));
        expected_2.add(buildChunk(11L, 22L, 200L));
        expected_2.add(buildChunk(12L, 33L, 300L));
        expected_2.add(buildChunk(13L, 11L, 100L));
        expected_2.add(buildChunk(14L, 22L, 200L));
        expected_2.add(buildChunk(15L, 33L, 300L));
        expected_2.add(buildChunk(16L, 11L, 100L));
        expected_2.add(buildChunk(17L, 22L, 200L));
        expected_2.add(buildChunk(18L, 33L, 300L));
    }


    @Test
    public void chunkDataTest() {
        ChunkService chunkService =
                new ChunkService(serverRepository,
                        chunkRepository,
                        patientRecordCountRepository,
                        patientMeasureLogRepository);
        chunkService.chunkData(measures);
        verify(chunkRepository, times(2)).saveAll(captor.capture());
        assertThat(captor.getAllValues().get(0)).isEqualTo(expected_1);
        assertThat(captor.getAllValues().get(1)).isEqualTo(expected_2);
    }

    private Chunk buildChunk(long l, long l2, long l3) {
        return Chunk.builder().patientId(l).serverId(l2).recordCount(l3).chunkStatus(ChunkStatus.PENDING).build();
    }

    private PatientRecordCount buildPatientRecordCount(long l, long l2) {
        return PatientRecordCount.builder().patientId(l).recordCount(l2).lastUpdated(ZonedDateTime.now()).build();
    }

}
