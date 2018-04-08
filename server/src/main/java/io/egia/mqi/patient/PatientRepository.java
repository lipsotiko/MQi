package io.egia.mqi.patient;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
	@Query(value="select p from Patient p join p.chunk c where c.serverId = :s and c.chunkId = :c order by p.patientId")
	List<Patient> findByServerIdAndChunkId(@Param("s") Long serverId, @Param("c") Long chunkId);
}
