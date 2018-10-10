package io.egia.mqi.visit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface CodeSetRepo extends JpaRepository<CodeSet, Long> {
    List<CodeSet> findByCodeSetGroupIdIn(Set<Long> relaventCodeSetGroupIds);
}
