package io.egia.mqi.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MeasureRepository extends JpaRepository<Measure, String> {

	List<Measure> findByMeasureId(Long measureId);
	
}