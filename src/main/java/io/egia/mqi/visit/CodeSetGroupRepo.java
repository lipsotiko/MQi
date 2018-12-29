package io.egia.mqi.visit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CodeSetGroupRepo extends JpaRepository<CodeSetGroup, Long> {
}
