package io.egia.mqi.chunk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;

@Service
public class ChunkService {
    private Logger log = LoggerFactory.getLogger(ChunkService.class);
    EntityManager entityManager;
    ChunkRepository chunkRepository;

    public ChunkService(EntityManager entityManager, ChunkRepository chunkRepository) {
        this.entityManager = entityManager;
        this.chunkRepository = chunkRepository;
    }

    public void chunkData() {
        log.info("Executing chunking process");
        Query query = entityManager.createNamedQuery("ChunkData");

        if (query.getSingleResult().toString().equals("-1")) {
            log.info("Chunking process failed.");
        }
    }

    Long getChunkId(Long serverId) {
        return chunkRepository.findOneByServerIdOrderByChunkIdAsc(serverId).get(0).getChunkId();
    }
}
