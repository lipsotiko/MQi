package io.egia.mqi.job;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

	List<Job> findByStatusOrderByJobIdAsc(Job.Status status);

	@Modifying
	@Transactional
	@Query(value="update Job j set j.status = ?2 where j.jobId = ?1")
	void updateJobStatus(Long jobId, Job.Status status);

}
