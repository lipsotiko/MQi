package io.egia.mqi.server;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
public class Server {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long serverId;
	private String serverName;
	private String serverPort;
	private String serverType;
	private String serverVersion;
	private int chunkSize;
	@Column(insertable = false)
	private Date lastUpdated;

}
