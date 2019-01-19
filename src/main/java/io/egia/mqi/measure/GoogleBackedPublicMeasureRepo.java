package io.egia.mqi.measure;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoogleBackedPublicMeasureRepo implements PublicMeasureRepo<Measure> {

    @Value("${mqi.properties.system.measureBucketUrl}")
    private String measureBucketUrl;

    public List<PublicMeasureListItem> getMeasureList() {
        RestTemplate restTemplate = new RestTemplate();
        PublicMeasuresListResponse publicMeasuresListResponse =
                restTemplate.getForObject(measureBucketUrl, PublicMeasuresListResponse.class);
        assert publicMeasuresListResponse != null;
        return publicMeasuresListResponse.getItems().stream().map((bucketObject) ->
                new PublicMeasureListItem(bucketObject.getName())).collect(Collectors.toList());
    }

    public Measure getMeasure(String publicMeasureObjectName) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        PublicMeasureListItem publicMeasureListItem = new PublicMeasureListItem();
        PublicMeasureResponse publicMeasureResponse =
                restTemplate.getForObject(measureBucketUrl + "/" + publicMeasureObjectName, PublicMeasureResponse.class);

        assert publicMeasureResponse != null;
        InputStream inputStream = new URL(publicMeasureResponse.getMediaLink()).openStream();
        StringWriter writer = new StringWriter();
        IOUtils.copy(inputStream, writer, "UTF8");
        String measureLogicJson = writer.toString();

        ObjectMapper mapper = new ObjectMapper();
        MeasureLogic measureLogic = mapper.readValue(measureLogicJson, MeasureLogic.class);
        Measure measure = new Measure();
        measure.setMeasureId(publicMeasureListItem.uuidFromObjectName(publicMeasureObjectName));
        measure.setMeasureLogic(measureLogic);
        measure.setLastUpdated(ZonedDateTime.now());
        measure.setMeasureJson(measureLogicJson);
        return measure;
    }
}
