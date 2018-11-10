package io.egia.mqi.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.egia.mqi.chunk.Chunk;
import io.egia.mqi.chunk.ChunkStatus;
import io.egia.mqi.job.Job;
import io.egia.mqi.job.JobStatus;
import io.egia.mqi.measure.Measure;
import io.egia.mqi.measure.MeasureLogic;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Optional;

public class Helpers {

    public static Measure getMeasureFromResource(String path, String measureFile) throws IOException {
        path = path + "/" + measureFile;
        File sampleMeasureJsonFile = new ClassPathResource(path).getFile();
        Measure measure = new Measure();
        measure.setMeasureName(measureFile);

        String measureLogicString = FileUtils.readFileToString(sampleMeasureJsonFile, "UTF-8");
        ObjectMapper mapper = new ObjectMapper();
        MeasureLogic measureLogic = mapper.readValue(measureLogicString, MeasureLogic.class);
        measure.setMeasureId(11L);
        measure.setMeasureLogic(measureLogic);
        measure.setLastUpdated(ZonedDateTime.parse("2007-12-03T10:15:30+01:00[Europe/Paris]"));
        measure.setMeasureJson(measureLogicString);
        return measure;
    }


    public static Optional<Job> job(Long id, Long initialPatientCount, JobStatus jobStatus) {
        return Optional.of(Job.builder()
                .jobId(id)
                .initialPatientCount(initialPatientCount)
                .jobStatus(jobStatus).build());
    }

    public static Optional<Chunk> chunk(Long serverId, Long patientId, Integer chunkGroup, ChunkStatus chunkStatus) {
        return Optional.of(Chunk.builder()
                .serverId(serverId)
                .patientId(patientId)
                .chunkGroup(chunkGroup)
                .chunkStatus(chunkStatus).build());
    }
}
