package io.egia.mqi.measure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeasureRepository extends JpaRepository<Measure, String> {

	List<Measure> findByMeasureId(Long measureId);


	@Query(value="select new io.egia.mqi.measure.MeasureListItem(m.measureId, m.fileName) from Measure m")
	List<MeasureListItem> findAllMeasureListItems();
	
}