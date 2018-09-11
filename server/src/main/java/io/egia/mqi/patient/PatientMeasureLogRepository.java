package io.egia.mqi.patient;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientMeasureLogRepository extends JpaRepository<PatientMeasureLog, Long> {

}
