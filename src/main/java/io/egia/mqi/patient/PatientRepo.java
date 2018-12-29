package io.egia.mqi.patient;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientRepo extends JpaRepository<Patient, Long> {
    @Query(value="select p from Patient p join Chunk c on p.patientId = c.patientId " +
            "where c.chunkGroup = :c")
    List<Patient> findByChunkGroup(@Param("c") Integer chunkGroup);
}
