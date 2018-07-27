package io.egia.mqi.measure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MeasureRepository extends JpaRepository<Measure, Long> {

	@Query(value="select new io.egia.mqi.measure.MeasureListItem(m.measureId, m.measureName) " +
			"from Measure m order by m.measureName")
	List<MeasureListItem> findAllMeasureListItems();

	@Query(value = "select m from Measure m join JobMeasure jm on m.measureId = jm.measureId where jm.jobId = :id")
    List<Measure> findAllByJobId(@Param("id")Long jobId);
}
