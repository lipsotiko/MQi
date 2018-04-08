package io.egia.mqi.visit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Long> {
	@Query(value="select v from Visit v join v.chunk c where c.serverId = :s and c.chunkId = :c order by v.patientId")
	List<Visit> findByServerIdAndChunkId(@Param("s") Long serverId, @Param("c") Long chunkId);
}
