package io.egia.mqi.integration;

import io.egia.mqi.chunk.Chunk;
import io.egia.mqi.chunk.ChunkRepo;
import io.egia.mqi.chunk.ChunkStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class ChunkRepoIntegrationTest {

    @Autowired
    private ChunkRepo chunkRepo;

    @Before
    public void setUp() {
        for(Long i = 25L; i > 0; i--) {
            Chunk chunk = new Chunk();
            chunk.setPatientId(i);
            chunk.setRecordCount(i);
            chunk.setServerId(i);
            chunk.setChunkStatus(ChunkStatus.PENDING);
            chunkRepo.saveAndFlush(chunk);
        }
    }

    @Test
    public void findFirstByServerIdAndChunkStatus() {
        Optional<Chunk> chunk =  chunkRepo.findTop1ByServerIdAndChunkStatus(1L, ChunkStatus.PENDING);
        assertThat(chunk.get().getServerId()).isEqualTo(1L);
    }

    @Test
    public void updateChunkStatusByServerIdAndChunkGroup() {
        assertThat(chunkRepo.findTop1ByServerIdAndChunkStatus(1L,ChunkStatus.DONE)).isNotPresent();
        chunkRepo.updateChunkStatusByServerIdAndChunkGroup(1L, 0, ChunkStatus.DONE);
        assertThat(chunkRepo.findTop1ByServerIdAndChunkStatus(1L,ChunkStatus.DONE)).isPresent();
    }

    @After
    public void tearDown() {
        List<Chunk> chunks = chunkRepo.findAll();
        for(Chunk c: chunks) {
            chunkRepo.delete(c);
        }
    }

}
