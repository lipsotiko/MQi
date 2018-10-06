package io.egia.mqi.chunk;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChunkRepo extends JpaRepository<Chunk, Long> {
    Optional<List<Chunk>> findTop5000ByServerIdAndChunkStatus(Long serverId, ChunkStatus chunkStatus);
}
