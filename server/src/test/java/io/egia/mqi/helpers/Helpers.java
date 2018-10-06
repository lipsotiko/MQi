package io.egia.mqi.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.egia.mqi.measure.Measure;
import io.egia.mqi.measure.MeasureLogic;
import io.egia.mqi.visit.CodeSet;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.time.ZonedDateTime;

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
        measure.setLastUpdated(ZonedDateTime.parse("2007-12-03T10:15:30+01:00[Europe/Paris]"));
        return measure;
    }

}
