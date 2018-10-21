package io.egia.mqi.measure;

import io.egia.mqi.chunk.Chunk;
import io.egia.mqi.chunk.ChunkRepo;
import io.egia.mqi.chunk.ChunkService;
import io.egia.mqi.chunk.ChunkStatus;
import io.egia.mqi.job.Job;
import io.egia.mqi.job.JobRepo;
import io.egia.mqi.job.JobStatus;
import io.egia.mqi.patient.Patient;
import io.egia.mqi.patient.PatientMeasureLogRepo;
import io.egia.mqi.patient.PatientRepo;
import io.egia.mqi.server.Server;
import io.egia.mqi.visit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class MeasureService {
    private Logger log = LoggerFactory.getLogger(MeasureService.class);
    private ChunkRepo chunkRepo;
    private ChunkService chunkService;
    private JobRepo jobRepo;
    private PatientRepo patientRepo;
    private VisitRepo visitRepo;
    private Processor processor;
    private CodeSetGroupRepo codeSetGroupRepo;
    private CodeSetRepo codeSetRepo;
    private final PatientMeasureLogRepo patientMeasureLogRepo;
    private final MeasureResultRepo measureResultRepo;

    MeasureService(JobRepo jobRepo, ChunkRepo chunkRepo, PatientRepo patientRepo,
                   VisitRepo visitRepo, ChunkService chunkService, Processor processor,
                   CodeSetGroupRepo codeSetGroupRepo, CodeSetRepo codeSetRepo,
                   PatientMeasureLogRepo patientMeasureLogRepo, MeasureResultRepo measureResultRepo) {
        this.chunkRepo = chunkRepo;
        this.chunkService = chunkService;
        this.jobRepo = jobRepo;
        this.patientRepo = patientRepo;
        this.visitRepo = visitRepo;
        this.processor = processor;
        this.codeSetGroupRepo = codeSetGroupRepo;
        this.codeSetRepo = codeSetRepo;
        this.patientMeasureLogRepo = patientMeasureLogRepo;
        this.measureResultRepo = measureResultRepo;
    }

    //TODO: Make process async
    public void process(Server server, Job job, List<Measure> measures) {
        if (measures.size() == 0) return;

        MeasureMetaData measureMetaData = new MeasureMetaData(getCodesSetsForMeasures(measures));
        chunkService.chunkData();
        Long serverId = server.getServerId();
        Optional<Chunk> currentChunk = chunkRepo.findTop1ByServerIdAndChunkStatus(serverId, ChunkStatus.PENDING);
        while (currentChunk.isPresent()) {
            int chunkGroup = currentChunk.get().getChunkGroup();
            log.debug(String.format("Processing chunk %s on server %s", chunkGroup, serverId));
            deleteMeasureResults(measures, serverId, chunkGroup);
            List<Patient> patients = patientRepo.findByServerIdAndChunkGroup(serverId, chunkGroup);
            List<Visit> visits = visitRepo.findByServerIdAndChunkGroup(serverId, chunkGroup);
            processor.process(measures, patients, visits, measureMetaData, ZonedDateTime.now());
            saveMeasureResults(processor);
            processor.clear();
            chunkRepo.updateChunkStatusByServerIdAndChunkGroup(serverId, chunkGroup, ChunkStatus.DONE);
            currentChunk = chunkRepo.findTop1ByServerIdAndChunkStatus(serverId, ChunkStatus.PENDING);
        }

        jobRepo.updateJobStatus(job.getJobId(), JobStatus.SUCCESS);
    }

    private void deleteMeasureResults(List<Measure> measures, Long serverId, int chunkGroup) {
        for (Measure m : measures) {
            patientMeasureLogRepo.deleteByChunkGroupAndServerIdAndMeasureId(chunkGroup, serverId, m.getMeasureId());
            measureResultRepo.deleteByChunkGroupAndServerIdAndMeasureId(chunkGroup, serverId, m.getMeasureId());
        }
    }

    private void saveMeasureResults(Processor processor) {
        patientMeasureLogRepo.saveAll(processor.getLog());
        measureResultRepo.saveAll(processor.getResults());
    }

    private List<CodeSet> getCodesSetsForMeasures(List<Measure> measures) {
        List<CodeSetGroup> allCodeSetGroups = codeSetGroupRepo.findAll();
        Set<Long> relevantCodeSetGroupIds = new HashSet<>();

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
