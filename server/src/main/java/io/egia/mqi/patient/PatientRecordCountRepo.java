package io.egia.mqi.patient;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientRecordCountRepo extends JpaRepository<PatientRecordCount, Long> {
    List<PatientRecordCount> findTop5000By();
}
