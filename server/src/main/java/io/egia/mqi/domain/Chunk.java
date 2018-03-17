package io.egia.mqi.domain;

import javax.persistence.*;

@NamedNativeQueries(value = {
		@NamedNativeQuery(name = "ChunkData", query = "select * from fn_chunk_data();")
})

@Entity
public class Chunk {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long patientId;
	private Long recordCnt;
	private Long serverId;
	private Long chunkId;
	
	public Long getPatientId() {
		return patientId;
	}

	public void setPatientId(Long patientId) {
		this.patientId = patientId;
	}

	public Long getRecordCnt() {
		return recordCnt;
	}

	public void setRecordCnt(Long recordCnt) {
		this.recordCnt = recordCnt;
	}

	public Long getServerId() {
		return serverId;
	}

	public void setServerId(Long serverId) {
		this.serverId = serverId;
	}
	
	public Long getChunk() {
		return chunkId;
	}

	public Long getChunkId() {
		return chunkId;
	}

	public void setChunkId(Long chunkId) {
		this.chunkId = chunkId;
	}
}
