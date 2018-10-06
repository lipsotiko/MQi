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
            chunk.setServerId(1L);
            chunk.setChunkStatus(ChunkStatus.PENDING);
            chunkRepo.saveAndFlush(chunk);
        }
    }

    @Test
    public void findFirstByServerIdAndChunkStatus() {
        Optional<List<Chunk>> chunk =  chunkRepo.findTop5000ByServerIdAndChunkStatus(1L, ChunkStatus.PENDING);
        assertThat(chunk.get().get(0).getServerId()).isEqualTo(1L);
    }

    @After
    public void tearDown() {
        List<Chunk> chunks = chunkRepo.findAll();
        for(Chunk c: chunks) {
            chunkRepo.delete(c);
        }
    }

}
