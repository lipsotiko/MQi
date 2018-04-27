package io.egia.mqi.chunk;

import lombok.Data;

import javax.persistence.*;

@NamedNativeQueries(value = {
		@NamedNativeQuery(name = "ChunkData", query = "select * from fn_chunk_data();")
})

@Data
@Entity
public class Chunk {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long patientId;
	private Long recordCnt;
	private Long serverId;
	private Long chunkId;
}
