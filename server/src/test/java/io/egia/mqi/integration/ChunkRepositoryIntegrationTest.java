package io.egia.mqi.integration;

import io.egia.mqi.chunk.Chunk;
import io.egia.mqi.chunk.ChunkRepository;
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
public class ChunkRepositoryIntegrationTest {

    @Autowired
    private ChunkRepository chunkRepository;

    @Before
    public void setUp() {
        for(Long i = 25L; i > 0; i--) {
            Chunk chunk = new Chunk();
            chunk.setPatientId(i);
            chunk.setRecordCnt(i);
            chunk.setServerId(1L);
            chunk.setChunkId(i);
            chunk.setChunkStatus(ChunkStatus.PENDING);
            chunkRepository.saveAndFlush(chunk);
        }
    }

    @Test
    public void findOneByServerIdOrderByChunkIdAsc() {
        Optional<Chunk> chunk =  chunkRepository.findFirstByServerIdAndChunkStatus(1L, ChunkStatus.PENDING);
        assertThat(chunk.get().getChunkId()).isEqualTo(1L);
    }

    @After
    public void tearDown() {
        List<Chunk> chunks = chunkRepository.findAll();
        for(Chunk c: chunks) {
            chunkRepository.delete(c);
        }
    }

}
