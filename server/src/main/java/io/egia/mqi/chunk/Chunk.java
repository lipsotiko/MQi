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
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long chunkId;
	private Long patientId;
	private Long recordCount;
	private Long serverId;
	private Long chunkGroup;
	private ChunkStatus chunkStatus;
}
