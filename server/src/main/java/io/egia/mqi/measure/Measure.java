package io.egia.mqi.measure;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.*;

import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonRawValue;

@Entity
public class Measure implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long measureId;
	private String fileName;
	@Column(columnDefinition="Blob") private byte[] fileBytes;
	@Column(updatable=false,insertable=false) private Date lastUpdated;
	
	public Long getMeasureId() {
		return this.measureId;
	}
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@JsonIgnore
	public byte[] getFileBytes() {
		return fileBytes;
	}

	public void setFileBytes(byte[] fileBytes) {
		this.fileBytes = fileBytes;
	}
	
	@JsonRawValue
	public String getMeasure() {
		JSONObject jsonObject = new JSONObject(new String(fileBytes));
		return jsonObject.toString();
	}
	
	@JsonIgnore
	public Date getLastUpdated() {
		return lastUpdated;
	}
	
	public String getLastUpdatedFormated() {
		SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd:HH:mm:SS");
		return DATE_FORMAT.format(lastUpdated).toString();
	}
}
