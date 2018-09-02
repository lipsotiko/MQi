package io.egia.mqi.server;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Server {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long serverId;
	private String serverName;
	private String serverPort;
	private String systemType;
	private String systemVersion;
	private int chunkSize;
	@Column(insertable = false)
	private Date lastUpdated;

}
