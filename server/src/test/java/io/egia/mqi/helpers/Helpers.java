package io.egia.mqi.helpers;

import io.egia.mqi.measure.Measure;
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
        measure.setMeasureJson(FileUtils.readFileToString(sampleMeasureJsonFile, "UTF-8"));
        return measure;
    }
}
