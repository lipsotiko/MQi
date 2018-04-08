package io.egia.mqi.job;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface JobMeasureRepository extends JpaRepository<JobMeasure, Long> {
	List<JobMeasure> findByJobId(Long jobId);

	@Transactional
	void deleteByJobMeasureId(Long jobMeasureId);
}
