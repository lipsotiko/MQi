package io.egia.mqi.measure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RuleParamRepository extends JpaRepository<RuleParam, Long> {
    Iterable<RuleParam> findAllByRuleNameOrderByRuleParamId(String ruleName);

    @Query(value = "select distinct ruleName from RuleParam order by ruleName")
    Iterable<String> findAllDistinctRules();
}
