package io.egia.mqi.version;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface VersionRepository extends JpaRepository<Version, String> {
	@Modifying
	@Transactional
    @Query(value="update Version v set v.versionId = :v")
    public void updateVersion(@Param("v") String versionId);
}
