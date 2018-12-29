package io.egia.mqi.chunk;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface ChunkRepo extends JpaRepository<Chunk, Long> {
    Optional<Chunk> findTop1ByChunkStatus(ChunkStatus chunkStatus);

    @Modifying
    @Transactional
    @Query(value = "update Chunk c set c.chunkStatus = ?2 where chunkGroup = ?1")
    void updateChunkStatusByChunkGroup(int chunkGroup, ChunkStatus chunkStatus);

    Long countByChunkStatus(ChunkStatus chunkStatus);

}
