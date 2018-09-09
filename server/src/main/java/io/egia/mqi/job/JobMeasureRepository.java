package io.egia.mqi.job;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobMeasureRepository extends JpaRepository<JobMeasure, Long> {
}
