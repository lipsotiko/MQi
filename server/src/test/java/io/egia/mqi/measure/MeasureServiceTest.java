package io.egia.mqi.measure;

import io.egia.mqi.chunk.Chunk;
import io.egia.mqi.chunk.ChunkRepo;
import io.egia.mqi.chunk.ChunkService;
import io.egia.mqi.chunk.ChunkStatus;
import io.egia.mqi.helpers.Helpers;
import io.egia.mqi.job.Job;
import io.egia.mqi.job.JobRepo;
import io.egia.mqi.job.JobStatus;
import io.egia.mqi.patient.Patient;
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
    @Mock
    private JobRepo jobRepo;
    @Mock
    private ChunkRepo chunkRepo;
    @Mock
    private PatientRepo patientRepo;
    @Mock
    private VisitRepo visitRepo;
    @Mock
    private ChunkService chunkService;
    private ProcessorSpy spy;
    @Mock
    private CodeSetGroupRepo codeSetGroupRepo;
    @Mock
    private CodeSetRepo codeSetRepo;
    @Mock
    PatientMeasureLogRepo patientMeasureLogRepo;
    @Mock
    MeasureResultRepo measureResultRepo;

    private MeasureService measureService;

    private Server server = Server.builder().serverId(11L).systemType(SystemType.PRIMARY).build();
    private Job job;

    @Before
    public void setUp() {
        spy = new ProcessorSpy();
        measureService = new MeasureService(
                jobRepo, chunkRepo, patientRepo, visitRepo, chunkService, spy,
                codeSetGroupRepo, codeSetRepo, patientMeasureLogRepo, measureResultRepo);

        job = new Job();
        job.setJobId(44L);
        job.setJobStatus(JobStatus.RUNNING);

        Chunk c = new Chunk();
        c.setPatientId(99L);
        Patient p = new Patient();
        p.setPatientId(99L);
        List<Patient> patients = new ArrayList<>(Collections.singletonList(p));
        when(patientRepo.findByServerIdAndChunkGroup(11L, 1)).thenReturn(patients);

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
    }

    @Test
    public void verifyMethodsWereCalled() {
        Measure measure = new Measure();
        measure.setMeasureName("Fake Measure");

        measureService.process(server, job, Collections.singletonList(measure));

        verify(chunkService, times(1)).chunkData();
        verify(chunkRepo, times(4)).findTop1ByServerIdAndChunkStatus(11L, ChunkStatus.PENDING);

        assertThat(spy.processWasCalledWithMeasures.get(0).getMeasureName()).isEqualTo("Fake Measure");
        assertThat(spy.processWasCalledWithPatients.get(0).getPatientId()).isEqualTo(99L);
        assertThat(spy.processWasCalledWithVisits.get(0).getPatientId()).isEqualTo(99L);

        verify(chunkRepo, times(1))
                .updateChunkStatusByServerIdAndChunkGroup(11L, 1, ChunkStatus.DONE);
        verify(chunkRepo, times(1))
                .updateChunkStatusByServerIdAndChunkGroup(11L, 2, ChunkStatus.DONE);
        verify(chunkRepo, times(1))
                .updateChunkStatusByServerIdAndChunkGroup(11L, 3, ChunkStatus.DONE);

        assertThat(spy.processWasCalled).isEqualTo(true);
        assertThat(spy.clearWasCalled).isEqualTo(true);

        verify(jobRepo, times(1)).updateJobStatus(job.getJobId(), JobStatus.SUCCESS);
    }

    @Test
    public void verifyNothingIsProcessedWhenNoMeasuresAreSupplied() {
        List<Measure> measures = Collections.emptyList();
        measureService.process(server, job, measures);
        verify(chunkService, times(0)).chunkData();
        assertThat(spy.processWasCalled).isEqualTo(false);
    }

    @Test
    public void codeSetsRelatedToAMeasureArePassedToTheProcessor() throws IOException {
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
        measureService.process(server, job, Collections.singletonList(measure));
        assertThat(spy.processWasCalledWithMeasureMetaData.getCodeSets())
                .isEqualTo(Collections.singletonList(codeSetA));
    }

    @Test
    public void measureResultsAndLongAreRemovedByChunkAndSavedWhenUpdated() throws IOException {
        List<MeasureResult> expected = new ArrayList<>();
        expected.add(MeasureResult.builder().patientId(1L).measureId(1L).resultCode("DENOMINATOR").build());

        Measure measure = Helpers.getMeasureFromResource("fixtures", "sampleMeasure2.json");
        measure.setMeasureId(11L);
        measureService.process(server, job, Collections.singletonList(measure));

        verify(measureResultRepo, times(1)).deleteByChunkGroupAndServerId(1, server.getServerId());
        verify(measureResultRepo, times(1)).deleteByChunkGroupAndServerId(2, server.getServerId());
        verify(measureResultRepo, times(1)).deleteByChunkGroupAndServerId(3, server.getServerId());

        verify(patientMeasureLogRepo, times(1)).deleteByChunkGroupAndServerId(1, server.getServerId());
        verify(patientMeasureLogRepo, times(1)).deleteByChunkGroupAndServerId(2, server.getServerId());
        verify(patientMeasureLogRepo, times(1)).deleteByChunkGroupAndServerId(3, server.getServerId());

        verify(measureResultRepo, times(3)).saveAll(expected);
    }
}
