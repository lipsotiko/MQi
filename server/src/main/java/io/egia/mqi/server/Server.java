package io.egia.mqi.server;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
public class Server implements Serializable {
	private static final long serialVersionUID = 1L;

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
