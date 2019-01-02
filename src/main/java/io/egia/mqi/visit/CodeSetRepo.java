package io.egia.mqi.visit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface CodeSetRepo extends JpaRepository<CodeSet, UUID> {
    List<CodeSet> findByCodeSetGroupIdIn(Set<UUID> relaventCodeSetGroupIds);
}
