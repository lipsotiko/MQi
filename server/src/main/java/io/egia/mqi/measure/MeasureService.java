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
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MeasureService {
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

        if(measures.size() == 0) {
            return;
        }

        chunkService.chunkData();

        Long serverId = server.getServerId();

        Optional<List<Chunk>> currentChunk = chunkRepo.findTop5000ByServerIdAndChunkStatus(
                serverId, ChunkStatus.PENDING);

        while (currentChunk.isPresent()) {
            List<Chunk> chunks = currentChunk.get();
            Set<Integer> chunkGroups = chunks.stream().map(Chunk::getChunkGroup).collect(Collectors.toSet());

            chunkGroups.forEach(cg -> {
                List<Patient> patients = patientRepo.findByServerIdAndChunkGroup(serverId, cg);
                List<Visit> visits = visitRepo.findByServerIdAndChunkGroup(serverId, cg);
                measureProcessor.process(measures, patients, visits, ZonedDateTime.now());
                measureProcessor.clear();
            });

            chunks.forEach(c -> c.setChunkStatus(ChunkStatus.DONE));
            chunkRepo.saveAll(chunks);
            currentChunk = chunkRepo.findTop5000ByServerIdAndChunkStatus(
                    serverId, ChunkStatus.PENDING);
        }

        jobRepo.updateJobStatus(job.getJobId(), JobStatus.SUCCESS);
    }
}
