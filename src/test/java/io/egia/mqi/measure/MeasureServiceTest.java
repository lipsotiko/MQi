package io.egia.mqi.measure;

import io.egia.mqi.chunk.ChunkRepo;
import io.egia.mqi.job.Job;
import io.egia.mqi.patient.Patient;
import io.egia.mqi.patient.PatientMeasureLog;
import io.egia.mqi.patient.PatientMeasureLogRepo;
import io.egia.mqi.patient.PatientRepo;
import io.egia.mqi.visit.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.*;

import static io.egia.mqi.chunk.ChunkStatus.PENDING;
import static io.egia.mqi.chunk.ChunkStatus.PROCESSED;
import static io.egia.mqi.helpers.Helpers.chunk;
import static io.egia.mqi.helpers.Helpers.getMeasureFromResource;
import static io.egia.mqi.job.JobStatus.RUNNING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MeasureServiceTest {

    @Mock
    private ChunkRepo chunkRepo;
    @Mock
    private PatientRepo patientRepo;
    @Mock
    private VisitRepo visitRepo;
    @Mock
    private CodeSetGroupRepo codeSetGroupRepo;
    @Mock
    private CodeSetRepo codeSetRepo;
    @Mock
    private PatientMeasureLogRepo patientMeasureLogRepo;
    @Mock
    private MeasureResultRepo measureResultRepo;
    private ProcessorSpy processorSpy;
    private Measure measure;
    private MeasureService measureService;
    private Patient p77, p88, p99;
    private Visit v199;

    @Before
    public void setUp() {
        processorSpy = new ProcessorSpy();
        measureService = new MeasureService(
                chunkRepo,
                patientRepo,
                visitRepo,
                processorSpy,
                codeSetGroupRepo,
                codeSetRepo,
                patientMeasureLogRepo,
                measureResultRepo);

        Job job = new Job();
        job.setId(44L);
        job.setJobStatus(RUNNING);

        p77 = new Patient();
        p77.setPatientId(77L);
        when(patientRepo.findByChunkGroup(1))
                .thenReturn(new ArrayList<>(Collections.singletonList(p77)));

        p88 = new Patient();
        p88.setPatientId(88L);
        when(patientRepo.findByChunkGroup(2))
                .thenReturn(new ArrayList<>(Collections.singletonList(p88)));

        p99 = new Patient();
        p99.setPatientId(99L);
        when(patientRepo.findByChunkGroup(3))
                .thenReturn(new ArrayList<>(Collections.singletonList(p99)));

        v199 = new Visit();
        v199.setVisitId(199L);
        v199.setPatientId(99L);
        List<Visit> visits = new ArrayList<>(Collections.singletonList(v199));
        when(visitRepo.findByChunkGroup(1)).thenReturn(visits);

        when(chunkRepo.findTop1ByChunkStatus(PENDING))
                .thenReturn(chunk(11L, 99L, 1, PENDING))
                .thenReturn(chunk(11L, 88L, 2, PENDING))
                .thenReturn(chunk(11L, 77L, 3, PENDING))
                .thenReturn(Optional.empty());

        measure = new Measure();
        measure.setMeasureName("Fake Measure");
    }

    @Test
    public void measure_process_was_called_with_patient_data() {
        measureService.process(Collections.singletonList(measure), null);
        assertThat(processorSpy.processWasCalled).isEqualTo(true);
        assertThat(processorSpy.processWasCalledWithPatients).containsExactly(p77, p88, p99);
        assertThat(processorSpy.processWasCalledWithVisits).containsExactly(v199);
    }

    @Test
    public void data_was_chunked() {
        measureService.process(Collections.singletonList(measure), null);

        verify(chunkRepo, times(4))
                .findTop1ByChunkStatus(PENDING);
        verify(chunkRepo, times(1))
                .updateChunkStatusByChunkGroup(1, PROCESSED);
        verify(chunkRepo, times(1))
                .updateChunkStatusByChunkGroup(2, PROCESSED);
        verify(chunkRepo, times(1))
                .updateChunkStatusByChunkGroup(3, PROCESSED);
    }

    @Test
    public void measure_processor_was_cleared() {
        measureService.process(Collections.singletonList(measure), null);
        assertThat(processorSpy.clearWasCalled).isEqualTo(true);
    }

    @Test
    public void nothing_is_proessed_when_no_measures_are_supplied() {
        List<Measure> measures = Collections.emptyList();
        measureService.process(measures, null);
        assertThat(processorSpy.processWasCalled).isEqualTo(false);
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
        Measure measure = getMeasureFromResource("fixtures", "sampleMeasure.json");
        measureService.process(Collections.singletonList(measure), null);
        assertThat(processorSpy.processWasCalledWithMeasureMetaData.getCodeSets())
                .isEqualTo(Collections.singletonList(codeSetA));
    }

    @Test
    public void measure_results_are_removed() throws IOException {
        Measure measure = getMeasureFromResource("fixtures", "sampleMeasure2.json");
        measureService.process(Collections.singletonList(measure), null);

        verify(measureResultRepo, times(1))
                .deleteByChunkGroupAndMeasureId(1, 11L);
        verify(measureResultRepo, times(1))
                .deleteByChunkGroupAndMeasureId(2, 11L);
        verify(measureResultRepo, times(1))
                .deleteByChunkGroupAndMeasureId(3, 11L);
    }

    @Test
    public void measure_results_are_saved() throws IOException {
        Measure measure = getMeasureFromResource("fixtures", "sampleMeasure2.json");
        measureService.process(Collections.singletonList(measure), null);

        verify(measureResultRepo, times(1)).saveAll(
                Arrays.asList(
                        MeasureResult.builder().patientId(77L).measureId(11L).resultCode("DENOMINATOR").build(),
                        MeasureResult.builder().patientId(88L).measureId(11L).resultCode("DENOMINATOR").build(),
                        MeasureResult.builder().patientId(99L).measureId(11L).resultCode("DENOMINATOR").build()
                )
        );
    }

    @Test
    public void measure_patient_logs_are_removed() throws IOException {
        Measure measure = getMeasureFromResource("fixtures", "sampleMeasure2.json");
        measureService.process(Collections.singletonList(measure), null);

        verify(patientMeasureLogRepo, times(1))
                .deleteByChunkGroupAndMeasureId(1, 11L);
        verify(patientMeasureLogRepo, times(1))
                .deleteByChunkGroupAndMeasureId(2, 11L);
        verify(patientMeasureLogRepo, times(1))
                .deleteByChunkGroupAndMeasureId(3, 11L);
    }

    @Test
    public void patient_measure_logs_are_saved() throws IOException {
        Measure measure = getMeasureFromResource("fixtures", "sampleMeasure2.json");
        measureService.process(Collections.singletonList(measure), null);

        verify(patientMeasureLogRepo, times(1)).saveAll(
                Arrays.asList(
                        PatientMeasureLog.builder().patientId(77L).measureId(11L).build(),
                        PatientMeasureLog.builder().patientId(88L).measureId(11L).build(),
                        PatientMeasureLog.builder().patientId(99L).measureId(11L).build()
                )
        );
    }
}
