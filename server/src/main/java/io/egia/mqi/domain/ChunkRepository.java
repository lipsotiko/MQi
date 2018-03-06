package io.egia.mqi.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChunkRepository extends JpaRepository<Chunk, Long> {
	List<Chunk> findTop1ByServerIdOrderByChunkIdAsc(Long serverId);
}