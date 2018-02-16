package io.egia.mqi.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobMeasureRepository extends JpaRepository<JobMeasure, Long> {
	List<JobMeasure> findByJobId(Long jobId);
}