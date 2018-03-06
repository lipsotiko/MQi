package io.egia.mqi.domain;

import java.io.Serializable;

import javax.persistence.*;

@Entity
@Table(name = "t_version")
public class Version implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id 
	@Column(name="version_id") private String versionId;
	@Transient private int majorVersion;	
	@Transient private int minorVersion;	
	@Transient private int patchVersion;
	
	public String getVersionId() {
		return versionId;
	}

	public void setVersionId(String versionId) {
		this.versionId = versionId;
		String[] v = this.versionId.split("\\.");
		majorVersion = Integer.parseInt(v[0]);
		minorVersion = Integer.parseInt(v[1]);
		patchVersion = Integer.parseInt(v[2]);
	}
	
	public int compareTo(Version v) {
		int i = 0;
		if (this.majorVersion < v.majorVersion) {
			i--;
		} else if (this.majorVersion > v.majorVersion) {
			i++;
		} else {
			if (this.minorVersion < v.minorVersion) {
				i--;
			} else if (this.minorVersion > v.minorVersion) {
				i++;
			} else {
				if (this.patchVersion < v.patchVersion) {
					i--;
				} else if (this.patchVersion > v.patchVersion) {
					i++;
				} else {
					i = 0;
				}
			}
		}
		return i;
	}
}
