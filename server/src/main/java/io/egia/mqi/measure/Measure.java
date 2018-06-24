package io.egia.mqi.measure;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import javax.persistence.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@Entity
public class Measure {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long measureId;
	private String measureName;
	@Column(columnDefinition="longvarchar") private String measureJson;
	@Column(updatable=false,insertable=false) private Date lastUpdated;

	@JsonRawValue
	public String getMeasureJson() {
		return measureJson;
	}

	@JsonIgnore
	public MeasureLogic getLogic() throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(measureJson, MeasureLogic.class);
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
