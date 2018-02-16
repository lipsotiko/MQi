package io.egia.mqi.service;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.egia.mqi.domain.ChunkRepository;

@Service
public class ChunkService {
	
	private Logger log = LoggerFactory.getLogger(ChunkService.class);
	
	@Autowired
	EntityManager em;
	
	@Autowired
	ChunkRepository chunkRepository;
	
	void chunkData() {
		log.info("Executing chunking process");
		Query query = em.createNamedQuery("Patient.chunkData");
		
		if (query.getSingleResult().toString().equals("-1")) {
			log.info("Chunking process failed.");
		}
	}
	
	Long getChunkId(Long serverId) {
		return chunkRepository.findTop1ByServerIdOrderByChunkIdAsc(serverId).get(0).getChunkId();
	}
}
