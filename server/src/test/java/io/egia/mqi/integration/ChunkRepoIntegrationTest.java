package io.egia.mqi.integration;

import io.egia.mqi.chunk.Chunk;
import io.egia.mqi.chunk.ChunkRepo;
import io.egia.mqi.chunk.ChunkStatus;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class ChunkRepoIntegrationTest extends AbstractRepositoryTest {

    @Autowired
    private ChunkRepo chunkRepo;

    @Test
    public void chunkRepo_findTop1ByServerIdAndChunkStatus() {
        Optional<Chunk> chunk =  chunkRepo.findTop1ByServerIdAndChunkStatus(1L, ChunkStatus.PENDING);
        assertThat(chunk.get().getServerId()).isEqualTo(1L);
    }

    @Test
    public void chunkRepo_updateChunkStatusByServerIdAndChunkGroup() {
        assertThat(chunkRepo.findTop1ByServerIdAndChunkStatus(1L,ChunkStatus.DONE)).isNotPresent();
        chunkRepo.updateChunkStatusByServerIdAndChunkGroup(1L, 0, ChunkStatus.DONE);
        assertThat(chunkRepo.findTop1ByServerIdAndChunkStatus(1L,ChunkStatus.DONE)).isPresent();
    }
}
