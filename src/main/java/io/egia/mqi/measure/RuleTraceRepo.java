package io.egia.mqi.measure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public interface RuleTraceRepo extends JpaRepository<RuleTrace, Long> {
    @Modifying
    @Transactional
    @Query(value="delete from RuleTrace r where r.patientId in ( " +
            "select patientId from Chunk c where c. chunkGroup = :c) " +
            "and r.measureId = :m")
    void deleteByChunkGroupAndMeasureId(@Param("c") Integer chunkGroup, @Param("m") UUID measureId);

    List<RuleTrace> findAllByMeasureIdAndPatientId(UUID measureId, Long patientId);
}
