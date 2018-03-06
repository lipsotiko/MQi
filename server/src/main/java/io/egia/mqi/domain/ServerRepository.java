package io.egia.mqi.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ServerRepository extends JpaRepository<Server, Long> {
	
	List<Server> findByServerNameAndServerPort(String serverName, String serverPort);
	
	List<Server> findByServerType(String serverType);
	
	@Modifying
	@Transactional
	@Query(value="update Server s set s.serverType = ?2, s.serverVersion = ?3 where s.serverId = ?1")
	public void updateServer(Long serverId, String serverType, String serverVersion);
	
}