package io.egia.mqi.measure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface MeasureResultRepo extends JpaRepository<MeasureResult, Long> {
    @Modifying
    @Transactional
    @Query(value="delete from MeasureResult p where p.patientId in ( " +
            "select patientId from Chunk c where c. chunkGroup = :c  and c.serverId = :s ) " +
            "and p.measureId = :m")
    void deleteByChunkGroupAndServerIdAndMeasureId(@Param("c") Integer chunkGroup, @Param("s") Long serverId, @Param("m") Long measureId);

}
