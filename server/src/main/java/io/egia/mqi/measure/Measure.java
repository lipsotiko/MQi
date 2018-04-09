package io.egia.mqi.measure;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonRawValue;

import javax.persistence.*;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
public class Measure implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long measureId;
	private String measureName;
	@Column(columnDefinition="longvarchar") private String measureLogic;
	@Column(updatable=false,insertable=false) private Date lastUpdated;
	
	public Long getMeasureId() {
		return this.measureId;
	}

	public Long setMeasureId() {
		return this.measureId;
	}

	public String getMeasureName() {
		return measureName;
	}

	public void setMeasureName(String measureName) {
		this.measureName = measureName;
	}

	@JsonRawValue
	public String getMeasureLogic() {
		return measureLogic;
	}

	public void setMeasureLogic(String measureLogic) {
		this.measureLogic = measureLogic;
	}
	
	@JsonIgnore
	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public String getLastUpdatedFormated() {
		SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd:HH:mm:SS");
		return DATE_FORMAT.format(lastUpdated).toString();
	}
}
