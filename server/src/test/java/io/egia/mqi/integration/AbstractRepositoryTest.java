package io.egia.mqi.integration;

import io.egia.mqi.chunk.Chunk;
import io.egia.mqi.chunk.ChunkRepository;
import io.egia.mqi.patient.Patient;
import io.egia.mqi.patient.PatientRepository;
import io.egia.mqi.visit.Visit;
import io.egia.mqi.visit.VisitCode;
import io.egia.mqi.visit.VisitCodeRepository;
import io.egia.mqi.visit.VisitRepository;
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
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
abstract class AbstractRepositoryTest {

    @Autowired
    private ChunkRepository chunkRepository;
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private VisitRepository visitRepository;
    @Autowired
    private VisitCodeRepository visitCodeRepository;

    @Before
    public void setUp() {
        for (Long i = 1L; i <= 10; i++) {
            Chunk chunk = new Chunk();
            chunk.setPatientId(i);
            chunk.setRecordCount(100L);
            chunk.setServerId(i);
            chunk.setChunkGroup(1);
            chunkRepository.saveAndFlush(chunk);

            Patient patient = new Patient();
            patient.setPatientId(i);
            patientRepository.saveAndFlush(patient);

            Visit visit = new Visit();
            visit.setVisitId(i);
            visit.setPatientId(i);
            Visit savedVisit = visitRepository.saveAndFlush(visit);

            VisitCode code = new VisitCode();
            code.setVisitCodeId(i);
            code.setVisitId(i);
            code.setCodeValue("abc");
            code.setCodeSystem("ICD_9");
            visitCodeRepository.saveAndFlush(code);

            savedVisit.setVisitCodes(Collections.singletonList(code));
            visitRepository.saveAndFlush(savedVisit);
        }
    }

    @After
    public void tearDown() {
        visitCodeRepository.deleteAll();
        visitRepository.deleteAll();
        patientRepository.deleteAll();
        chunkRepository.deleteAll();
    }
}
