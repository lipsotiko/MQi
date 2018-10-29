package io.egia.mqi.chunk;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface ChunkRepo extends JpaRepository<Chunk, Long> {
    Optional<Chunk> findTop1ByServerIdAndChunkStatus(Long serverId, ChunkStatus chunkStatus);

    @Modifying
    @Transactional
    @Query(value = "update Chunk c set c.chunkStatus = ?3 where c.serverId = ?1  and chunkGroup = ?2")
    void updateChunkStatusByServerIdAndChunkGroup(Long serverId, int chunkGroup, ChunkStatus chunkStatus);

    Long countByChunkStatus(ChunkStatus chunkStatus);

}
