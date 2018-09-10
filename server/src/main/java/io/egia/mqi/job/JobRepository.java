package io.egia.mqi.job;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

	Optional<List<Job>> findByJobStatusOrderByJobIdAsc(JobStatus jobStatus);

	@Modifying
	@Transactional
	@Query(value="update Job j set j.jobStatus = ?2 where j.jobId = ?1")
	void updateJobStatus(Long jobId, JobStatus jobStatus);

}
