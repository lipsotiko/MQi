package io.egia.mqi.server;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ServerRepository extends JpaRepository<Server, Long> {
	Server findOneByServerNameAndServerPort(String serverName, String serverPort);

	Server findOneByServerType(String serverType);

	@Modifying
	@Transactional
	@Query(value="update Server s set s.serverType = ?2, s.serverVersion = ?3 where s.serverId = ?1")
	void updateServer(Long serverId, String serverType, String serverVersion);
}
