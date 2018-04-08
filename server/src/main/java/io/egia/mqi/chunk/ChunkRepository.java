package io.egia.mqi.chunk;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChunkRepository extends JpaRepository<Chunk, Long> {
	List<Chunk> findTop1ByServerIdOrderByChunkIdAsc(Long serverId);
}
