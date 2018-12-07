package io.egia.mqi.integration;

import io.egia.mqi.chunk.Chunk;
import io.egia.mqi.chunk.ChunkRepo;
import io.egia.mqi.measure.MeasureResult;
import io.egia.mqi.measure.MeasureResultRepo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class MeasureResultRepoTest {

    @Autowired
    private MeasureResultRepo measureResultRepo;

    @Autowired
    private ChunkRepo chunkRepo;

    @Before
    public void setUp() {
        chunkRepo.save(Chunk.builder().chunkGroup(1).patientId(1L).build());
        chunkRepo.save(Chunk.builder().chunkGroup(2).patientId(2L).build());
        measureResultRepo.save(MeasureResult.builder().patientId(1L).measureId(1L).build());
        measureResultRepo.save(MeasureResult.builder().patientId(2L).measureId(2L).build());
        measureResultRepo.save(MeasureResult.builder().patientId(3L).measureId(1L).build());
        measureResultRepo.save(MeasureResult.builder().patientId(4L).measureId(2L).build());
        assertThat(chunkRepo.count()).isEqualTo(2L);
        assertThat(measureResultRepo.count()).isEqualTo(4L);
    }

    @Test
    public void measureResultRepo_deleteByChunkGroupAndServerIdAndMeasureId() {
        measureResultRepo.deleteByChunkGroupAndMeasureId(1, 1L);
        assertThat(measureResultRepo.count()).isEqualTo(3);
    }

    @After
    public void tearDown() {
        measureResultRepo.deleteAll();
        chunkRepo.deleteAll();
    }
}
