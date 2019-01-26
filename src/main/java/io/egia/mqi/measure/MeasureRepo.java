package io.egia.mqi.measure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MeasureRepo extends JpaRepository<Measure, UUID> {

    @Query(value = "select new io.egia.mqi.measure.MeasureListItem(m.measureId, m.measureName, m.lastUpdated) " +
            "from Measure m order by m.measureName")
    List<MeasureListItem> findAllMeasureListItems();

}
