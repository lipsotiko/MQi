package io.egia.mqi.measure;

import io.egia.mqi.chunk.Chunk;
import io.egia.mqi.chunk.ChunkRepo;
import io.egia.mqi.patient.Patient;
import io.egia.mqi.patient.PatientMeasureLogRepo;
import io.egia.mqi.patient.PatientRepo;
import io.egia.mqi.visit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.*;

import static io.egia.mqi.chunk.ChunkStatus.PENDING;
import static io.egia.mqi.chunk.ChunkStatus.PROCESSED;

@Service
public class MeasureService {
    private Logger log = LoggerFactory.getLogger(MeasureService.class);
    private ChunkRepo chunkRepo;
    private PatientRepo patientRepo;
    private VisitRepo visitRepo;
    private Processor processor;
    private CodeSetGroupRepo codeSetGroupRepo;
    private CodeSetRepo codeSetRepo;
    private final PatientMeasureLogRepo patientMeasureLogRepo;
    private final MeasureResultRepo measureResultRepo;

    MeasureService(ChunkRepo chunkRepo,
                   PatientRepo patientRepo,
                   VisitRepo visitRepo,
                   Processor processor,
                   CodeSetGroupRepo codeSetGroupRepo,
                   CodeSetRepo codeSetRepo,
                   PatientMeasureLogRepo patientMeasureLogRepo,
                   MeasureResultRepo measureResultRepo) {
        this.chunkRepo = chunkRepo;
        this.patientRepo = patientRepo;
        this.visitRepo = visitRepo;
        this.processor = processor;
        this.codeSetGroupRepo = codeSetGroupRepo;
        this.codeSetRepo = codeSetRepo;
        this.patientMeasureLogRepo = patientMeasureLogRepo;
        this.measureResultRepo = measureResultRepo;
    }

    public void process(List<Measure> measures, Long jobId) throws MeasureServiceException {

        if (measures.size() == 0) return;

        MeasureMetaData measureMetaData = new MeasureMetaData(getCodesSetsForMeasures(measures));

        Optional<Chunk> currentChunk = chunkRepo.findTop1ByChunkStatus(PENDING);
        while (currentChunk.isPresent()) {
            int chunkGroup = currentChunk.get().getChunkGroup();
            log.debug(String.format("Processing chunk %s ", chunkGroup));
            deleteMeasureResults(measures, chunkGroup);
            List<Patient> patients = patientRepo.findByChunkGroup(chunkGroup);
            List<Visit> visits = visitRepo.findByChunkGroup(chunkGroup);
            try {
                processor.process(measures, patients, visits, measureMetaData, ZonedDateTime.now(), jobId);
            } catch (MeasureProcessorException e) {
                throw new MeasureServiceException("Error: Could not execute measure processor");
            }
            saveMeasureResults(processor);
            processor.clear();
            chunkRepo.updateChunkStatusByChunkGroup(chunkGroup, PROCESSED);
            currentChunk = chunkRepo.findTop1ByChunkStatus(PENDING);
        }
    }

    private void deleteMeasureResults(List<Measure> measures, int chunkGroup) {
        for (Measure m : measures) {
            patientMeasureLogRepo.deleteByChunkGroupAndMeasureId(chunkGroup, m.getMeasureId());
            measureResultRepo.deleteByChunkGroupAndMeasureId(chunkGroup, m.getMeasureId());
        }
    }

    private void saveMeasureResults(Processor processor) {
        patientMeasureLogRepo.saveAll(processor.getLog());
        measureResultRepo.saveAll(processor.getResults());
    }

    private List<CodeSet> getCodesSetsForMeasures(List<Measure> measures) {
        List<CodeSetGroup> allCodeSetGroups = codeSetGroupRepo.findAll();
        Set<UUID> relevantCodeSetGroupIds = new HashSet<>();

        for (Measure m : measures) {
            for (CodeSetGroup csg : allCodeSetGroups) {
                if (m.getMeasureJson().contains(csg.getGroupName())) {
                    relevantCodeSetGroupIds.add(csg.getId());
                }
            }
        }

        return codeSetRepo.findByCodeSetGroupIdIn(relevantCodeSetGroupIds);
    }
}
