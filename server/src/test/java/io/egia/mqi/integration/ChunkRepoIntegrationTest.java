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
            chunk.setChunkStatus(ChunkStatus.PENDING);
            chunkRepo.saveAndFlush(chunk);
        }
    }

    @Test
    public void chunkRepo_findTop1ByServerIdAndChunkStatus() {
        Optional<Chunk> chunk =  chunkRepo.findTop1ByServerIdAndChunkStatus(1L, ChunkStatus.PENDING);
        if(!chunk.isPresent()) {
            fail("Chunk was not present...");
        } else {
            assertThat(chunk.get().getServerId()).isEqualTo(1L);
        }
    }

    @Test
    public void chunkRepo_updateChunkStatusByServerIdAndChunkGroup() {
        assertThat(chunkRepo.findTop1ByServerIdAndChunkStatus(1L,ChunkStatus.DONE)).isNotPresent();
        chunkRepo.updateChunkStatusByServerIdAndChunkGroup(1L, 0, ChunkStatus.DONE);
        assertThat(chunkRepo.findTop1ByServerIdAndChunkStatus(1L,ChunkStatus.DONE)).isPresent();
    }

    @Test
    public void chunkRepo_countByChunkStatus() {
        assertThat(chunkRepo.countByChunkStatus(ChunkStatus.DONE)).isEqualTo(0L);
        assertThat(chunkRepo.countByChunkStatus(ChunkStatus.PENDING)).isEqualTo(10L);
    }
}
