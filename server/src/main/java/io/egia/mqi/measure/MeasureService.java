package io.egia.mqi.measure;

import io.egia.mqi.chunk.Chunk;
import io.egia.mqi.chunk.ChunkRepository;
import io.egia.mqi.chunk.ChunkService;
import io.egia.mqi.job.Job;
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
    private MeasureRepository measureRepository;
    private ChunkRepository chunkRepository;
    private ChunkService chunkService;
    private JobRepository jobRepository;
    private ServerService serverService;
    private PatientRepository patientRepository;
    private VisitRepository visitRepository;
    private MeasureProcessor measureProcessor;

    MeasureService(MeasureRepository measureRepository
            , ChunkRepository chunkRepository
            , ChunkService chunkService
            , JobRepository jobRepository
            , ServerService serverService
            , PatientRepository patientRepository
            , VisitRepository visitRepository
            , MeasureProcessor measureProcessor) {
        this.measureRepository = measureRepository;
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

    public void process() throws UnknownHostException {

        Optional<List<Job>> jobsToProcess = jobRepository.findByStatusOrderByJobIdAsc(Job.Status.PENDING);
        if(!jobsToProcess.isPresent()) {
            return;
        }

        Job currentJob = jobsToProcess.get().get(0);
        while (currentJob != null) {
            Server thisServer = serverService.getServerFromHostNameAndPort(serverPort);
            jobRepository.updateJobStatus(currentJob.getJobId(), Job.Status.RUNNING);
            chunkService.chunkData();
            Chunk chunk = chunkRepository.findOneByServerIdOrderByChunkIdAsc(thisServer.getServerId()).get(0);
            List<Patient> patients = patientRepository.findByChunkServerIdAndChunkId(thisServer.getServerId(), chunk.getChunkId());
            List<Visit> visits = visitRepository.findByChunkServerIdAndChunkChunkId(thisServer.getServerId(), chunk.getChunkId());
            List<Measure> measuresToBeProcessed = measureRepository.findAllByJobId(currentJob.getJobId());
            measureProcessor.initProcessor(measuresToBeProcessed, patients, visits);
            measureProcessor.process();
            jobRepository.updateJobStatus(currentJob.getJobId(), Job.Status.SUCCESS);

            jobsToProcess = jobRepository.findByStatusOrderByJobIdAsc(Job.Status.PENDING);
            if (!jobsToProcess.isPresent()) {
                return;
            }

            currentJob = jobsToProcess.get().get(0);
        }
    }
}
