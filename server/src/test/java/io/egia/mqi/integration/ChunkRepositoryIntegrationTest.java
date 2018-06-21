package io.egia.mqi.integration;

import io.egia.mqi.StandaloneConfig;
import io.egia.mqi.chunk.Chunk;
import io.egia.mqi.chunk.ChunkRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {StandaloneConfig.class})
public class ChunkRepositoryIntegrationTest {

    @Autowired
    ChunkRepository chunkRepository;

    @Before
    public void setUp() {
        for(Long i = 25L; i > 0; i--) {
            Chunk chunk = new Chunk();
            chunk.setPatientId(i);
            chunk.setRecordCnt(i);
            chunk.setServerId(1L);
            chunk.setChunkId(i);
            chunkRepository.save(chunk);
        }
    }

    @Test
    public void findOneByServerIdOrderByChunkIdAsc() {
        List<Chunk> chunks =  chunkRepository.findByServerIdOrderByChunkIdAsc(1L);
        assertThat(chunks.get(0).getChunkId()).isEqualTo(1L);
    }

    @After
    public void tearDown() {
        List<Chunk> chunks = chunkRepository.findAll();
        for(Chunk c: chunks) {
            chunkRepository.delete(c);
        }
    }

}
