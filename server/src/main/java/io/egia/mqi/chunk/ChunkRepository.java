package io.egia.mqi.chunk;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface ChunkRepository extends JpaRepository<Chunk, Long> {

    Optional<Chunk> findFirstByServerIdAndChunkStatus(Long serverId, ChunkStatus chunkStatus);

    @Modifying
    @Transactional
    @Query(value = "update Chunk c set c.chunkStatus = :chunkStatus where c.chunkId = :chunkId")
    void updateChunkStatus(@Param("chunkId") Long chunkId, @Param("chunkStatus") ChunkStatus chunkStatus);

}
