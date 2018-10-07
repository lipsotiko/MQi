package io.egia.mqi.job;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface JobRepo extends JpaRepository<Job, Long> {

	@Modifying
	@Transactional
	@Query(value="update Job j set j.jobStatus = ?2 where j.jobId = ?1")
	void updateJobStatus(Long jobId, JobStatus jobStatus);

}