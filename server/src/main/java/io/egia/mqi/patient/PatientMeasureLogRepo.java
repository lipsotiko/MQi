package io.egia.mqi.patient;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PatientMeasureLogRepo extends JpaRepository<PatientMeasureLog, Long> {
    @Modifying
    @Transactional
    @Query(value="delete from PatientMeasureLog p where p.patientId in (" +
            "select patientId from Chunk c where c. chunkGroup = :c  and c.serverId = :s ) " +
            "and p.measureId = :m")
    void deleteByChunkGroupAndServerIdAndMeasureId(@Param("c") Integer chunkGroup, @Param("s") Long serverId, @Param("m") Long measureId);
}