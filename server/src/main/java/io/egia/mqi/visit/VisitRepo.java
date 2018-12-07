package io.egia.mqi.visit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VisitRepo extends JpaRepository<Visit, Long> {
    @Query(value="select v from Visit v join Chunk c on v.patientId = c.patientId where c.chunkGroup = :c")
    List<Visit> findByChunkGroup(@Param("c") Integer chunkGroup);
}
