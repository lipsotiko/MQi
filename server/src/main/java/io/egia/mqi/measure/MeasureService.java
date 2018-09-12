package io.egia.mqi.measure;

import io.egia.mqi.chunk.Chunk;
import io.egia.mqi.chunk.ChunkRepository;
import io.egia.mqi.chunk.ChunkService;
import io.egia.mqi.chunk.ChunkStatus;
import io.egia.mqi.job.Job;
import io.egia.mqi.job.JobRepository;
import io.egia.mqi.job.JobStatus;
import io.egia.mqi.patient.Patient;
import io.egia.mqi.patient.PatientRepository;
import io.egia.mqi.server.Server;
import io.egia.mqi.visit.Visit;
import io.egia.mqi.visit.VisitRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MeasureService {
    private Logger log = LoggerFactory.getLogger(MeasureService.class);

    private ChunkRepository chunkRepository;
    private ChunkService chunkService;
    private JobRepository jobRepository;
    private PatientRepository patientRepository;
    private VisitRepository visitRepository;
    private MeasureProcessor measureProcessor;

    MeasureService(ChunkRepository chunkRepository,
                   ChunkService chunkService,
                   JobRepository jobRepository,
                   PatientRepository patientRepository,
                   VisitRepository visitRepository,
                   MeasureProcessor measureProcessor) {
        this.chunkRepository = chunkRepository;
        this.chunkService = chunkService;
        this.jobRepository = jobRepository;
        this.patientRepository = patientRepository;
        this.visitRepository = visitRepository;
        this.measureProcessor = measureProcessor;
    }

    //TODO: Make process async
    public void process(Server server, Job job, List<Measure> measures) {

        chunkService.chunkData();

        Optional<Chunk> currentChunk = chunkRepository.findFirstByServerIdAndChunkStatus(
                server.getServerId(), ChunkStatus.PENDING);

        if (currentChunk.isPresent()) {
            while (currentChunk.isPresent()) {
                Long chunkGroup = currentChunk.get().getChunkGroup();
                List<Patient> patients = patientRepository.findByServerIdAndChunkGroup(server.getServerId(), chunkGroup);
                List<Visit> visits = visitRepository.findByServerIdAndChunkChunkGroup(server.getServerId(), chunkGroup);
                measureProcessor.process(measures, patients, visits);
                measureProcessor.clear();
                chunkRepository.updateChunkStatus(chunkGroup, ChunkStatus.DONE);
                currentChunk = chunkRepository.findFirstByServerIdAndChunkStatus(
                        server.getServerId(), ChunkStatus.PENDING);
            }
        }

        jobRepository.updateJobStatus(job.getJobId(), JobStatus.SUCCESS);
    }
}
