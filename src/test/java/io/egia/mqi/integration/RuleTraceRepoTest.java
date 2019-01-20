package io.egia.mqi.integration;

import io.egia.mqi.chunk.Chunk;
import io.egia.mqi.chunk.ChunkRepo;
import io.egia.mqi.measure.RuleTrace;
import io.egia.mqi.measure.RuleTraceRepo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static io.egia.mqi.helpers.Helpers.UUID1;
import static io.egia.mqi.helpers.Helpers.UUID2;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class RuleTraceRepoTest {

    @Autowired
    private RuleTraceRepo ruleTraceRepo;

    @Autowired
    private ChunkRepo chunkRepo;

    @Before
    public void setUp() {
        chunkRepo.save(Chunk.builder().chunkGroup(1).patientId(1L).build());
        chunkRepo.save(Chunk.builder().chunkGroup(2).patientId(2L).build());
        ruleTraceRepo.save(RuleTrace.builder().patientId(1L).measureId(UUID1).build());
        ruleTraceRepo.save(RuleTrace.builder().patientId(2L).measureId(UUID2).build());
        ruleTraceRepo.save(RuleTrace.builder().patientId(3L).measureId(UUID1).build());
        ruleTraceRepo.save(RuleTrace.builder().patientId(4L).measureId(UUID2).build());
        assertThat(chunkRepo.count()).isEqualTo(2L);
        assertThat(ruleTraceRepo.count()).isEqualTo(4L);
    }

    @Test
    public void measureResultRepo_deleteByChunkGroupAndServerIdAndMeasureId() {
        ruleTraceRepo.deleteByChunkGroupAndMeasureId(1, UUID1);
        assertThat(ruleTraceRepo.count()).isEqualTo(3);
    }

    @After
    public void tearDown() {
        ruleTraceRepo.deleteAll();
        chunkRepo.deleteAll();
    }
}
