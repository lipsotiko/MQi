package io.egia.mqi.patient;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PatientMeasureLogRepo extends JpaRepository<PatientMeasureLog, Long> {
    @Transactional
    void deleteByPatientIdAndMeasureId(Long patientId, Long measureId);
}
