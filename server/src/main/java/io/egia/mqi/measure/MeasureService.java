package io.egia.mqi.measure;

import io.egia.mqi.chunk.*;
import io.egia.mqi.job.*;
import io.egia.mqi.job.JobStatus;
import io.egia.mqi.job.JobRepository;
import io.egia.mqi.patient.Patient;
import io.egia.mqi.patient.PatientRepository;
import io.egia.mqi.server.Server;
import io.egia.mqi.server.ServerService;
import io.egia.mqi.visit.Visit;
import io.egia.mqi.visit.VisitRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.UnknownHostException;
import java.util.List;
import java.util.Optional;

@Service
public class MeasureService {
    private Logger log = LoggerFactory.getLogger(MeasureService.class);

    private ChunkRepository chunkRepository;
    private ChunkService chunkService;
    private JobRepository jobRepository;
    private ServerService serverService;
    private PatientRepository patientRepository;
    private VisitRepository visitRepository;
    private MeasureProcessor measureProcessor;

    MeasureService(ChunkRepository chunkRepository
            , ChunkService chunkService
            , JobRepository jobRepository
            , ServerService serverService
            , PatientRepository patientRepository
            , VisitRepository visitRepository
            , MeasureProcessor measureProcessor) {
        this.chunkRepository = chunkRepository;
        this.chunkService = chunkService;
        this.jobRepository = jobRepository;
        this.serverService = serverService;
        this.patientRepository = patientRepository;
        this.visitRepository = visitRepository;
        this.measureProcessor = measureProcessor;
    }

    @Value("${server.port}")
    private String serverPort;

    public void process(Job job, List<Measure> measures) throws UnknownHostException {
        Server server = serverService.getServerFromHostNameAndPort(serverPort);
        chunkService.chunkData();

        Optional<Chunk> currentChunk = chunkRepository.findFirstByServerIdAndChunkStatus(
                server.getServerId(), ChunkStatus.PENDING);

        if (currentChunk.isPresent()) {
            while (currentChunk.isPresent()) {
                Long chunkId = currentChunk.get().getChunkId();
                List<Patient> patients = patientRepository.findByServerIdAndChunkId(server.getServerId(), chunkId);
                List<Visit> visits = visitRepository.findByServerIdAndChunkChunkId(server.getServerId(), chunkId);
                measureProcessor.initProcessor(measures, patients, visits);
                measureProcessor.process();
                measureProcessor.clear();
                chunkRepository.updateChunkStatus(chunkId, ChunkStatus.DONE);
                currentChunk = chunkRepository.findFirstByServerIdAndChunkStatus(
                        server.getServerId(), ChunkStatus.PENDING);
            }
        }

        jobRepository.updateJobStatus(job.getJobId(), JobStatus.SUCCESS);
    }
}
