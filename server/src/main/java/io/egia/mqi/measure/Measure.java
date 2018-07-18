package io.egia.mqi.measure;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import javax.persistence.*;
import java.io.IOException;
import java.util.Date;

@Data
@Entity
public class Measure {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long measureId;
	private String measureName;

	@JsonIgnore
	private String measureJson;

	@Transient
	private MeasureLogic measureLogic;

	@Column(updatable=false,insertable=false) private Date lastUpdated;
	
	@JsonIgnore
	public Date getLastUpdated() {
		return lastUpdated;
	}

//	public String getLastUpdatedFormated() {
//		SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd:HH:mm:SS");
//		return DATE_FORMAT.format(lastUpdated).toString();
//	}

	@JsonProperty("measureLogic")
	public MeasureLogic getMeasureLogic() {
		if (measureJson != null) {
			ObjectMapper mapper = new ObjectMapper();
			MeasureLogic measureLogic = null;
			try {
				measureLogic = mapper.readValue(measureJson, MeasureLogic.class);
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.measureLogic = measureLogic;
		}

		return measureLogic;
	}

	public void setMeasureJson(MeasureLogic measureLogic) {
		if (measureLogic != null) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				measureJson = mapper.writeValueAsString(measureLogic);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void setMeasureJson(String measureJson) {
		this.measureJson = measureJson;
	}
}
