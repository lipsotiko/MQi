package io.egia.mqi.measure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PublicMeasuresRepository {

    @Value("${mqi.properties.system.measureBucketUrl}")
    private String measureBucketUrl;

    public List<String> getMeasureList() {
        RestTemplate restTemplate = new RestTemplate();
        PublicMeasuresListResponse publicMeasuresListResponse =
                restTemplate.getForObject(measureBucketUrl, PublicMeasuresListResponse.class);
        return publicMeasuresListResponse.getItems().stream().map(BucketObject::getName).collect(Collectors.toList());
    }
}
