package io.egia.mqi.measure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RuleParamRepository extends JpaRepository<RuleParam, Long> {
    Optional<RuleParam> findAllByRuleNameOrderByDisplayOrder(String ruleName);
}
