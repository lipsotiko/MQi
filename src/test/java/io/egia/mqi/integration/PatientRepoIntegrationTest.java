package io.egia.mqi.integration;

import io.egia.mqi.chunk.Chunk;
import io.egia.mqi.chunk.ChunkRepo;
import io.egia.mqi.patient.Patient;
import io.egia.mqi.patient.PatientRepo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class PatientRepoIntegrationTest {

    @Autowired
    private PatientRepo patientRepo;
    @Autowired
    private ChunkRepo chunkRepo;

    @Before
    public void setUp() {
        int chunkGroup = 0;
        for (Long i = 1L; i <= 2; i++) {
            Chunk chunk = new Chunk();
            chunk.setPatientId(i);
            chunk.setChunkGroup(chunkGroup);
            chunkRepo.saveAndFlush(chunk);

            Patient patient = new Patient();
            patient.setPatientId(i);
            patientRepo.saveAndFlush(patient);
            chunkGroup++;
        }
    }

    @Test
    public void patientRepo_findByChunkGroup() {
        List<Patient> subject = patientRepo.findByChunkGroup(0);
        assertThat(subject.size()).isEqualTo(1);
    }

    @After
    public void tearDown() {
        patientRepo.deleteAll();
        chunkRepo.deleteAll();
    }
}
