package io.egia.mqi.measure;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MeasureLogic {
    private String description;
    private String minimumSystemVersion;
    private List<Steps> logic;
}
