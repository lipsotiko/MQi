package io.egia.mqi.measure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeasureRepo extends JpaRepository<Measure, Long> {

	@Query(value="select new io.egia.mqi.measure.MeasureListItem(m.measureId, m.measureName) " +
			"from Measure m order by m.measureName")
	List<MeasureListItem> findAllMeasureListItems();

}
