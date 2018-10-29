package io.egia.mqi.measure;

import io.egia.mqi.chunk.Chunk;
import io.egia.mqi.chunk.ChunkRepo;
import io.egia.mqi.chunk.ChunkService;
import io.egia.mqi.chunk.ChunkStatus;
import io.egia.mqi.helpers.Helpers;
import io.egia.mqi.job.Job;
import io.egia.mqi.job.JobStatus;
import io.egia.mqi.patient.Patient;
import io.egia.mqi.patient.PatientMeasureLog;
import io.egia.mqi.patient.PatientMeasureLogRepo;
import io.egia.mqi.patient.PatientRepo;
import io.egia.mqi.server.Server;
import io.egia.mqi.server.SystemType;
import io.egia.mqi.visit.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MeasureServiceTest {

    @Mock private ChunkRepo chunkRepo;
    @Mock private PatientRepo patientRepo;
    @Mock private VisitRepo visitRepo;
    private ProcessorSpy spy;
    @Mock private CodeSetGroupRepo codeSetGroupRepo;
    @Mock private CodeSetRepo codeSetRepo;
    @Mock private PatientMeasureLogRepo patientMeasureLogRepo;
    @Mock private MeasureResultRepo measureResultRepo;
    private Measure measure;
    private MeasureService measureService;
    private Server server = Server.builder().serverId(11L).systemType(SystemType.PRIMARY).build();
    private Job job;

    @Before
    public void setUp() {
        spy = new ProcessorSpy();
        measureService = new MeasureService(
                chunkRepo, patientRepo, visitRepo, spy, codeSetGroupRepo,
                codeSetRepo, patientMeasureLogRepo, measureResultRepo);

        job = new Job();
        job.setJobId(44L);
        job.setJobStatus(JobStatus.RUNNING);

        Patient p77 = new Patient();
        p77.setPatientId(77L);
        when(patientRepo.findByServerIdAndChunkGroup(11L, 1)).thenReturn(
                new ArrayList<>(Collections.singletonList(p77)));

        Patient p88 = new Patient();
        p88.setPatientId(88L);
        when(patientRepo.findByServerIdAndChunkGroup(11L, 2)).thenReturn(
                new ArrayList<>(Collections.singletonList(p88)));

        Patient p99 = new Patient();
        p99.setPatientId(99L);
        when(patientRepo.findByServerIdAndChunkGroup(11L, 3)).thenReturn(
                new ArrayList<>(Collections.singletonList(p99)));

        Visit v = new Visit();
        v.setPatientId(99L);
        List<Visit> visits = new ArrayList<>(Collections.singletonList(v));
        visits.add(v);
        when(visitRepo.findByServerIdAndChunkGroup(11L, 1)).thenReturn(visits);

        Chunk firstChunkPending = Chunk.builder().serverId(11L).patientId(99L).chunkGroup(1).chunkStatus(ChunkStatus.PENDING).build();
        Chunk secondChunkPending = Chunk.builder().serverId(11L).patientId(88L).chunkGroup(2).chunkStatus(ChunkStatus.PENDING).build();
        Chunk thirdChunkPending = Chunk.builder().serverId(11L).patientId(77L).chunkGroup(3).chunkStatus(ChunkStatus.PENDING).build();

        when(chunkRepo.findTop1ByServerIdAndChunkStatus(11L, ChunkStatus.PENDING))
                .thenReturn(Optional.of(firstChunkPending))
                .thenReturn(Optional.of(secondChunkPending))
                .thenReturn(Optional.of(thirdChunkPending))
                .thenReturn(Optional.empty());

        measure = new Measure();
        measure.setMeasureName("Fake Measure");
    }

    @Test
    public void measure_process_was_called() {
        measureService.process(server, Collections.singletonList(measure));
        assertThat(spy.processWasCalled).isEqualTo(true);
    }

    @Test
    public void data_was_chunked() {
        measureService.process(server, Collections.singletonList(measure));

        verify(chunkRepo, times(4)).findTop1ByServerIdAndChunkStatus(11L, ChunkStatus.PENDING);

        verify(chunkRepo, times(1))
                .updateChunkStatusByServerIdAndChunkGroup(11L, 1, ChunkStatus.DONE);
        verify(chunkRepo, times(1))
                .updateChunkStatusByServerIdAndChunkGroup(11L, 2, ChunkStatus.DONE);
        verify(chunkRepo, times(1))
                .updateChunkStatusByServerIdAndChunkGroup(11L, 3, ChunkStatus.DONE);
    }

    @Test
    public void measure_processor_was_cleared() {
        measureService.process(server, Collections.singletonList(measure));
        assertThat(spy.clearWasCalled).isEqualTo(true);
    }

    @Test
    public void nothing_is_proessed_when_no_measures_are_supplied() {
        List<Measure> measures = Collections.emptyList();
        measureService.process(server, measures);
        assertThat(spy.processWasCalled).isEqualTo(false);
    }

    @Test
    public void measure_codesets_are_passed_to_processor() throws IOException {
        CodeSetGroup codeSetGroupA = CodeSetGroup.builder().id(1L).groupName("CODE_SET_A").build();
        List<CodeSetGroup> codeSetGroups = new ArrayList<CodeSetGroup>() {{
            add(codeSetGroupA);
            add(codeSetGroupA);
        }};

        when(codeSetGroupRepo.findAll()).thenReturn(codeSetGroups);

        CodeSet codeSetA = CodeSet.builder().codeSetGroup(codeSetGroupA).build();
        when(codeSetRepo.findByCodeSetGroupIdIn(new HashSet<Long>() {{
            add(1L);
        }}))
                .thenReturn(Collections.singletonList(codeSetA));
        Measure measure = Helpers.getMeasureFromResource("fixtures", "sampleMeasure.json");
        measureService.process(server, Collections.singletonList(measure));
        assertThat(spy.processWasCalledWithMeasureMetaData.getCodeSets())
                .isEqualTo(Collections.singletonList(codeSetA));
    }

    @Test
    public void measure_results_are_removed() throws IOException {
        Measure measure = Helpers.getMeasureFromResource("fixtures", "sampleMeasure2.json");
        measureService.process(server, Collections.singletonList(measure));

        verify(measureResultRepo, times(1)).deleteByChunkGroupAndServerIdAndMeasureId(1, server.getServerId(), 11L);
        verify(measureResultRepo, times(1)).deleteByChunkGroupAndServerIdAndMeasureId(2, server.getServerId(), 11L);
        verify(measureResultRepo, times(1)).deleteByChunkGroupAndServerIdAndMeasureId(3, server.getServerId(), 11L);
    }

    @Test
    public void measure_results_are_saved() throws IOException {
        Measure measure = Helpers.getMeasureFromResource("fixtures", "sampleMeasure2.json");
        measureService.process(server, Collections.singletonList(measure));

        verify(measureResultRepo, times(1)).saveAll(
                Collections.singletonList(
                        MeasureResult.builder().patientId(77L).measureId(11L).resultCode("DENOMINATOR").build()
                )
        );

        verify(measureResultRepo, times(1)).saveAll(
                Collections.singletonList(
                        MeasureResult.builder().patientId(88L).measureId(11L).resultCode("DENOMINATOR").build()
                )
        );

        verify(measureResultRepo, times(1)).saveAll(
                Collections.singletonList(
                        MeasureResult.builder().patientId(99L).measureId(11L).resultCode("DENOMINATOR").build()
                )
        );
    }

    @Test
    public void measure_patient_logs_are_removed() throws IOException {
        List<MeasureResult> expected = new ArrayList<>();
        expected.add(MeasureResult.builder().patientId(1L).measureId(1L).resultCode("DENOMINATOR").build());

        Measure measure = Helpers.getMeasureFromResource("fixtures", "sampleMeasure2.json");
        measureService.process(server, Collections.singletonList(measure));

        verify(patientMeasureLogRepo, times(1)).deleteByChunkGroupAndServerIdAndMeasureId(1, server.getServerId(), 11L);
        verify(patientMeasureLogRepo, times(1)).deleteByChunkGroupAndServerIdAndMeasureId(2, server.getServerId(), 11L);
        verify(patientMeasureLogRepo, times(1)).deleteByChunkGroupAndServerIdAndMeasureId(3, server.getServerId(), 11L);
    }

    @Test
    public void patient_measure_logs_are_saved() throws IOException {
        Measure measure = Helpers.getMeasureFromResource("fixtures", "sampleMeasure2.json");
        measureService.process(server, Collections.singletonList(measure));

        verify(patientMeasureLogRepo, times(1)).saveAll(
                Collections.singletonList(PatientMeasureLog.builder().patientId(77L).measureId(11L).build())
        );
        verify(patientMeasureLogRepo, times(1)).saveAll(
                Collections.singletonList(PatientMeasureLog.builder().patientId(88L).measureId(11L).build())
        );
        verify(patientMeasureLogRepo, times(1)).saveAll(
                Collections.singletonList(PatientMeasureLog.builder().patientId(99L).measureId(11L).build())
        );
    }
}
