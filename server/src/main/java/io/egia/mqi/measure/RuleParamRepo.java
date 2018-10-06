package io.egia.mqi.measure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface RuleParamRepo extends JpaRepository<RuleParam, Long> {

}
