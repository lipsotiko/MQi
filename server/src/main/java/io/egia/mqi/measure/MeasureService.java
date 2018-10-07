package io.egia.mqi.measure;

import io.egia.mqi.chunk.Chunk;
import io.egia.mqi.chunk.ChunkRepo;
import io.egia.mqi.chunk.ChunkService;
import io.egia.mqi.chunk.ChunkStatus;
import io.egia.mqi.job.Job;
import io.egia.mqi.job.JobRepo;
import io.egia.mqi.job.JobStatus;
import io.egia.mqi.patient.Patient;
import io.egia.mqi.patient.PatientRepo;
import io.egia.mqi.server.Server;
import io.egia.mqi.visit.Visit;
import io.egia.mqi.visit.VisitRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MeasureService {
    private Logger log = LoggerFactory.getLogger(MeasureService.class);
    private ChunkRepo chunkRepo;
    private ChunkService chunkService;
    private JobRepo jobRepo;
    private PatientRepo patientRepo;
    private VisitRepo visitRepo;
    private MeasureProcessor measureProcessor;

    MeasureService(ChunkRepo chunkRepo,
                   ChunkService chunkService,
                   JobRepo jobRepo,
                   PatientRepo patientRepo,
                   VisitRepo visitRepo,
                   MeasureProcessor measureProcessor) {
        this.chunkRepo = chunkRepo;
        this.chunkService = chunkService;
        this.jobRepo = jobRepo;
        this.patientRepo = patientRepo;
        this.visitRepo = visitRepo;
        this.measureProcessor = measureProcessor;
    }

    //TODO: Make process async
    public void process(Server server, Job job, List<Measure> measures) {
        if (measures.size() == 0) return;

        chunkService.chunkData();

        Long serverId = server.getServerId();

        Optional<Chunk> currentChunk = chunkRepo.findTop1ByServerIdAndChunkStatus(serverId, ChunkStatus.PENDING);

        while (currentChunk.isPresent()) {
            Chunk chunk = currentChunk.get();
            int chunkGroup = chunk.getChunkGroup();
            log.debug(String.format("Processing chunk %s on server %s", chunkGroup, serverId));
            List<Patient> patients = patientRepo.findByServerIdAndChunkGroup(serverId, chunkGroup);
            List<Visit> visits = visitRepo.findByServerIdAndChunkGroup(serverId, chunkGroup);
            measureProcessor.process(measures, patients, visits, ZonedDateTime.now());
            measureProcessor.clear();
            chunkRepo.updateChunkStatusByServerIdAndChunkGroup(serverId, chunkGroup, ChunkStatus.DONE);
            currentChunk = chunkRepo.findTop1ByServerIdAndChunkStatus(serverId, ChunkStatus.PENDING);
        }

        jobRepo.updateJobStatus(job.getJobId(), JobStatus.SUCCESS);
    }
}
