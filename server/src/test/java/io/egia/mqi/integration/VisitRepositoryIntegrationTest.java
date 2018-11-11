package io.egia.mqi.integration;

import io.egia.mqi.chunk.Chunk;
import io.egia.mqi.chunk.ChunkRepo;
import io.egia.mqi.visit.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;
import java.util.List;

import static io.egia.mqi.visit.CodeSystem.ICD_9;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class VisitRepositoryIntegrationTest {

    @Autowired private VisitRepo visitRepo;
    @Autowired private VisitCodeRepo visitCodeRepo;
    @Autowired private ChunkRepo chunkRepo;

    @Before
    public void setUp() {
        for (Long i = 1L; i <= 2; i++) {
            Chunk chunk = new Chunk();
            chunk.setPatientId(i);
            chunk.setServerId(i);
            chunk.setChunkGroup(0);
            chunkRepo.saveAndFlush(chunk);

            Visit visit = new Visit();
            visit.setVisitId(i);
            visit.setPatientId(i);
            Visit savedVisit = visitRepo.saveAndFlush(visit);

            VisitCode code = new VisitCode();
            code.setVisitCodeId(i);
            code.setVisitId(i);
            code.setCodeValue("abc");
            code.setCodeSystem(ICD_9);
            visitCodeRepo.saveAndFlush(code);

            savedVisit.setVisitCodes(Collections.singletonList(code));
            visitRepo.saveAndFlush(savedVisit);
        }
    }

    @Test
    public void visitRepo_findByServerIdAndChunkGroup() {
        List<Visit> subject = visitRepo.findByServerIdAndChunkGroup(1L,0);
        assertThat(subject.size()).isEqualTo(1);
        assertThat(subject.get(0).getVisitCodes().get(0).getCodeSystem()).isEqualTo(ICD_9);
        assertThat(subject.get(0).getVisitCodes().get(0).getCodeValue()).isEqualTo("abc");
    }

    @After
    public void tearDown() {
        visitRepo.deleteAll();
        visitCodeRepo.deleteAll();
        chunkRepo.deleteAll();
    }
}
