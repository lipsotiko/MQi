package io.egia.mqi.measure;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Entity
public class Measure {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "measureId", updatable = false, nullable = false)
    private UUID measureId;
    private String measureName;

    @JsonIgnore
    private String measureJson;

    @Transient
    private MeasureLogic measureLogic;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ssa z", timezone = "America/New_York")
    private ZonedDateTime lastUpdated;

    @JsonProperty("measureLogic")
    public MeasureLogic getMeasureLogic() {

        if (measureLogic != null) {
            return measureLogic;
        }

        if (measureJson != null) {
            ObjectMapper mapper = new ObjectMapper();

            try {
                return mapper.readValue(measureJson, MeasureLogic.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
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
