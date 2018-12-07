package io.egia.mqi.integration;

import io.egia.mqi.chunk.Chunk;
import io.egia.mqi.chunk.ChunkRepo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Optional;

import static io.egia.mqi.chunk.ChunkStatus.PENDING;
import static io.egia.mqi.chunk.ChunkStatus.PROCESSED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class ChunkRepoIntegrationTest {

    @Autowired
    private ChunkRepo chunkRepo;

    @Before
    public void setUp() {
        for (long i = 1L; i <= 10; i++) {
            Chunk chunk = new Chunk();
            chunk.setChunkStatus(PENDING);
            chunkRepo.saveAndFlush(chunk);
        }
    }

    @Test
    public void chunkRepo_findTop1ByChunkStatus() {
        Optional<Chunk> chunk = chunkRepo.findTop1ByChunkStatus(PENDING);
        assertThat(chunk.get()).isNotNull();
    }

    @Test
    public void chunkRepo_updateChunkStatusByChunkGroup() {
        assertThat(chunkRepo.findTop1ByChunkStatus(PROCESSED)).isNotPresent();
        chunkRepo.updateChunkStatusByChunkGroup(0, PROCESSED);
        assertThat(chunkRepo.findTop1ByChunkStatus(PROCESSED)).isPresent();
    }

    @Test
    public void chunkRepo_countByChunkStatus() {
        assertThat(chunkRepo.countByChunkStatus(PROCESSED)).isEqualTo(0L);
        assertThat(chunkRepo.countByChunkStatus(PENDING)).isEqualTo(10L);
    }

    @After
    public void tearDown() {
        chunkRepo.deleteAll();
    }
}
