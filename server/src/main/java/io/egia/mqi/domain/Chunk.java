package io.egia.mqi.domain;

import javax.persistence.*;

@NamedNativeQueries(value = {
		@NamedNativeQuery(name = "Patient.chunkData", query = "select * from fn_chunk_data();") 
})

@Entity
@Table(name = "t_chunk")
public class Chunk {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="patient_id") private Long patientId;
	@Column(name="record_cnt") private Long recordCnt;
	@Column(name="server_id") private Long serverId;
	@Column(name="chunk_id") private Long chunkId;
	
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
