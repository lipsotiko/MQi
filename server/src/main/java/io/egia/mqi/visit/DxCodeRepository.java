package io.egia.mqi.visit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DxCodeRepository extends JpaRepository<VisitDxCode, Long> {

}
