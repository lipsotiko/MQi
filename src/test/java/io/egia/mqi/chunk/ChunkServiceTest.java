package io.egia.mqi.chunk;

import io.egia.mqi.job.Job;
import io.egia.mqi.job.JobRepo;
import io.egia.mqi.measure.Measure;
import io.egia.mqi.patient.PatientRecordCount;
import io.egia.mqi.patient.PatientRecordCountRepo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static io.egia.mqi.chunk.ChunkStatus.PENDING;
import static io.egia.mqi.helpers.Helpers.UUID1;
import static io.egia.mqi.helpers.Helpers.UUID2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ChunkServiceTest {

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

    private List<Chunk> expected_1 = new ArrayList<>();
    private List<Chunk> expected_2 = new ArrayList<>();

    private Job job;

    private ChunkService chunkService;

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

        when(patientRecordCountRepo.findBy(any()))
                .thenReturn(firstPatientRecordCounts)
                .thenReturn(secondPatientRecordCounts)
                .thenReturn(Collections.emptyList());

        Measure measureUpdatedYesterday = new Measure();
        measureUpdatedYesterday.setMeasureId(UUID1);
        measureUpdatedYesterday.setLastUpdated(ZonedDateTime.now().minusDays(1));

        Measure measureUpdatedToday = new Measure();
        measureUpdatedToday.setMeasureId(UUID2);
        measureUpdatedToday.setLastUpdated(ZonedDateTime.now());

        //Expected results
        expected_1.add(buildChunk(1L, 100, 0));
        expected_1.add(buildChunk(2L, 200, 0));
        expected_1.add(buildChunk(3L, 300, 0));
        expected_1.add(buildChunk(4L, 100, 0));
        expected_1.add(buildChunk(5L, 200, 0));
        expected_1.add(buildChunk(6L, 300, 0));
        expected_1.add(buildChunk(7L, 100, 0));
        expected_1.add(buildChunk(8L, 200, 0));
        expected_1.add(buildChunk(9L, 300, 0));

        expected_2.add(buildChunk(10L, 100, 1));
        expected_2.add(buildChunk(11L, 200, 1));
        expected_2.add(buildChunk(12L, 300, 1));
        expected_2.add(buildChunk(13L, 100, 1));
        expected_2.add(buildChunk(14L, 200, 1));
        expected_2.add(buildChunk(15L, 300, 1));
        expected_2.add(buildChunk(16L, 100, 1));
        expected_2.add(buildChunk(17L, 200, 1));
        expected_2.add(buildChunk(18L, 300, 1));

        job = Job.builder().build();

        chunkService =
                new ChunkService(chunkRepo, jobRepo, patientRecordCountRepo);
        ReflectionTestUtils.setField(chunkService, "pageSize", 10);
    }

    @Test
    public void data_is_chunked() {
        chunkService.chunkData(job);
        verify(chunkRepo, times(2)).saveAll(chunkCaptor.capture());
        assertThat(chunkCaptor.getAllValues().get(0)).isEqualTo(expected_1);
        assertThat(chunkCaptor.getAllValues().get(1)).isEqualTo(expected_2);
    }

    @Test
    public void initial_patient_count_is_saved_to_job() {
        chunkService.chunkData(job);
        verify(jobRepo).saveAndFlush(jobCaptor.capture());
        assertThat(jobCaptor.getAllValues().get(0).getInitialPatientCount()).isEqualTo(18);
    }

    @Test
    public void page_size_is_rounded_up() {
        when(patientRecordCountRepo.count()).thenReturn(50L);
        chunkService.chunkData(mock(Job.class));
        verify(patientRecordCountRepo,times(5)).findBy(any());
    }

    private Chunk buildChunk(long patientId, long recordCount, int chunkGroup) {
        return Chunk.builder().patientId(patientId).recordCount(recordCount).chunkGroup(chunkGroup).chunkStatus(PENDING).build();
    }

    private PatientRecordCount buildPatientRecordCount(long l, long l2) {
        return PatientRecordCount.builder().patientId(l).recordCount(l2).lastUpdated(ZonedDateTime.now()).build();
    }

}
