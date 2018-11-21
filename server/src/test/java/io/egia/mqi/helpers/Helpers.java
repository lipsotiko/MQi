package io.egia.mqi.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.egia.mqi.chunk.Chunk;
import io.egia.mqi.chunk.ChunkStatus;
import io.egia.mqi.job.Job;
import io.egia.mqi.job.JobStatus;
import io.egia.mqi.measure.Measure;
import io.egia.mqi.measure.MeasureLogic;
import io.egia.mqi.measure.MeasureMetaData;
import io.egia.mqi.patient.Patient;
import io.egia.mqi.patient.PatientData;
import io.egia.mqi.visit.CodeSet;
import io.egia.mqi.visit.CodeSetGroup;
import io.egia.mqi.visit.Visit;
import io.egia.mqi.visit.VisitCode;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.*;

import static io.egia.mqi.visit.CodeSystem.ICD_10;

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

    public static MeasureMetaData measureMetaData(String resource) {
        if (resource.equals("sampleMeasure.json")) {
            CodeSetGroup codeSetGroupA = CodeSetGroup.builder().groupName("CODE_SET_A").build();
            CodeSet codeSetA = CodeSet.builder().codeSetGroup(codeSetGroupA).codeSystem(ICD_10).codeValue("123").build();
            return new MeasureMetaData(Collections.singletonList(codeSetA));
        }
        return null;
    }

    public static PatientData patientData(Long patientId, String resource) {
        if (resource.equals("sampleMeasure.json")) {
            PatientData patientData = new PatientData(patientId);
            Patient patient = new Patient();
            Date dob = new GregorianCalendar(1986, Calendar.APRIL, 28).getTime();
            patient.setDateOfBirth(dob);
            patientData.addPatientRecord(patient);

            VisitCode visitCode = new VisitCode();
            visitCode.setCodeValue("123");
            visitCode.setCodeSystem(ICD_10);
            Visit visit = new Visit();
            visit.setVisitCodes(Collections.singletonList(visitCode));
            patientData.addPatientRecord(visit);
            return patientData;
        }
        return null;
    }

    public static Optional<Job> job(Long id, Long initialPatientCount, JobStatus jobStatus) {
        return Optional.of(Job.builder()
                .id(id)
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
