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
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class MeasureService {
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

        chunkService.chunkData(measures);

        Optional<List<Chunk>> currentChunk = chunkRepository.findTop5000ByServerIdAndChunkStatus(
                server.getServerId(), ChunkStatus.PENDING);

        while (currentChunk.isPresent()) {
            List<Chunk> chunks = currentChunk.get();
            List<Long> patientIds = chunks.stream().map(Chunk::getPatientId).collect(Collectors.toList());
            List<Patient> patients = patientRepository.findAllById(patientIds);
            List<Visit> visits = visitRepository.findAllById(patientIds);
            measureProcessor.process(measures, patients, visits);
            measureProcessor.clear();
            chunks.forEach(c -> c.setChunkStatus(ChunkStatus.DONE));
            chunkRepository.saveAll(chunks);
            currentChunk = chunkRepository.findTop5000ByServerIdAndChunkStatus(
                    server.getServerId(), ChunkStatus.PENDING);
        }

        jobRepository.updateJobStatus(job.getJobId(), JobStatus.SUCCESS);
    }
}
