package io.egia.mqi.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.egia.mqi.measure.Measure;
import io.egia.mqi.measure.MeasureLogic;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;

public class Helpers {

    public static Measure getMeasureFromResource(String path, String measureFile) throws IOException {
        path = path+"/"+measureFile;
        File sampleMeasureJsonFile = new ClassPathResource(path).getFile();
        Measure measure = new Measure();
        measure.setMeasureName(measureFile);

        String measureLogicString =  FileUtils.readFileToString(sampleMeasureJsonFile, "UTF-8");
        ObjectMapper mapper = new ObjectMapper();
        MeasureLogic measureLogic = mapper.readValue(measureLogicString, MeasureLogic.class);
        measure.setMeasureLogic(measureLogic);

        return measure;
    }

}
