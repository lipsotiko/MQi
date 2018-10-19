package io.egia.mqi.integration;

import io.egia.mqi.chunk.Chunk;
import io.egia.mqi.chunk.ChunkRepo;
import io.egia.mqi.chunk.ChunkStatus;
import io.egia.mqi.job.Job;
import io.egia.mqi.job.JobRepo;
import io.egia.mqi.job.JobStatus;
import io.egia.mqi.patient.Patient;
import io.egia.mqi.patient.PatientRepo;
import io.egia.mqi.visit.*;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
abstract class AbstractRepositoryTest {

    @Autowired private ChunkRepo chunkRepo;
    @Autowired private PatientRepo patientRepo;
    @Autowired private VisitRepo visitRepo;
    @Autowired private VisitCodeRepo visitCodeRepo;
    @Autowired private JobRepo jobRepo;

    @Before
    public void setUp() {
        for (Long i = 1L; i <= 10; i++) {
            Chunk chunk = new Chunk();
            chunk.setPatientId(i);
            chunk.setRecordCount(100L);
            chunk.setServerId(i);
            chunk.setChunkGroup(0);
            chunk.setChunkStatus(ChunkStatus.PENDING);
            chunkRepo.saveAndFlush(chunk);

            Patient patient = new Patient();
            patient.setPatientId(i);
            patientRepo.saveAndFlush(patient);

            Visit visit = new Visit();
            visit.setVisitId(i);
            visit.setPatientId(i);
            Visit savedVisit = visitRepo.saveAndFlush(visit);

            VisitCode code = new VisitCode();
            code.setVisitCodeId(i);
            code.setVisitId(i);
            code.setCodeValue("abc");
            code.setCodeSystem(CodeSystem.ICD_9);
            visitCodeRepo.saveAndFlush(code);

            savedVisit.setVisitCodes(Collections.singletonList(code));
            visitRepo.saveAndFlush(savedVisit);
        }

        for(int i = 5; i > 0; i--) {
            Job job = new Job();
            job.setJobStatus(JobStatus.PENDING);
            jobRepo.saveAndFlush(job);
        }
    }

    @After
    public void tearDown() {
        visitCodeRepo.deleteAll();
        visitRepo.deleteAll();
        patientRepo.deleteAll();
        chunkRepo.deleteAll();
        jobRepo.deleteAll();
    }
}
