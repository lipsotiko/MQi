package io.egia.mqi.integration;

import io.egia.mqi.chunk.Chunk;
import io.egia.mqi.chunk.ChunkRepo;
import io.egia.mqi.chunk.ChunkStatus;
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

    @Autowired private ChunkRepo chunkRepo;

    @Before
    public void setUp() {
        for (Long i = 1L; i <= 10; i++) {
            Chunk chunk = new Chunk();
            chunk.setServerId(i);
            chunk.setChunkStatus(PENDING);
            chunkRepo.saveAndFlush(chunk);
        }
    }

    @Test
    public void chunkRepo_findTop1ByServerIdAndChunkStatus() {
        Optional<Chunk> chunk =  chunkRepo.findTop1ByServerIdAndChunkStatus(1L, PENDING);
        if(!chunk.isPresent()) {
            fail("Chunk was not present...");
        } else {
            assertThat(chunk.get().getServerId()).isEqualTo(1L);
        }
    }

    @Test
    public void chunkRepo_updateChunkStatusByServerIdAndChunkGroup() {
        assertThat(chunkRepo.findTop1ByServerIdAndChunkStatus(1L,PROCESSED)).isNotPresent();
        chunkRepo.updateChunkStatusByServerIdAndChunkGroup(1L, 0, PROCESSED);
        assertThat(chunkRepo.findTop1ByServerIdAndChunkStatus(1L, PROCESSED)).isPresent();
    }

    @Test
    public void chunkRepo_countByChunkStatus() {
        assertThat(chunkRepo.countByChunkStatus(PROCESSED)).isEqualTo(0L);
        assertThat(chunkRepo.countByChunkStatus(PENDING)).isEqualTo(10L);
    }
}
