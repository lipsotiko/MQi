package io.egia.mqi.chunk;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NamedNativeQueries(value = {
		@NamedNativeQuery(name = "ChunkData", query = "select * from fn_chunk_data();")
})

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Chunk {
	
	@Id
	private Long patientId;
	private Long recordCnt;
	private Long serverId;
	private Long chunkId;
	private ChunkStatus chunkStatus;
}
