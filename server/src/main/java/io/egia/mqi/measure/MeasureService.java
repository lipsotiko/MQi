package io.egia.mqi.measure;

import io.egia.mqi.chunk.Chunk;
import io.egia.mqi.chunk.ChunkRepository;
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

@Service
public class MeasureService {
    private Logger log = LoggerFactory.getLogger(MeasureService.class);
    private MeasureRepository measureRepository;
    private ChunkRepository chunkRepository;
    private JobRepository jobRepository;
    private ServerService serverService;
    private PatientRepository patientRepository;
    private VisitRepository visitRepository;
    private MeasureProcessor measureProcessor;

    MeasureService(MeasureRepository measureRepository
            , ChunkRepository chunkRepository
            , JobRepository jobRepository
            , ServerService serverService
            , PatientRepository patientRepository
            , VisitRepository visitRepository
            , MeasureProcessor measureProcessor) {
        this.measureRepository = measureRepository;
        this.chunkRepository = chunkRepository;
        this.jobRepository = jobRepository;
        this.serverService = serverService;
        this.patientRepository = patientRepository;
        this.visitRepository = visitRepository;
        this.measureProcessor = measureProcessor;
    }

    @Value("${server.port}")
    private String serverPort;

    public void process() throws UnknownHostException {
        Server thisServer = serverService.getServerFromHostNameAndPort(serverPort);
        Job pendingJob = jobRepository.findByStatusOrderByJobIdAsc(Job.Status.PENDING).get(0);
        jobRepository.updateJobStatus(pendingJob.getJobId(), Job.Status.RUNNING);
        Chunk chunk = chunkRepository.findByServerIdOrderByChunkIdAsc(thisServer.getServerId()).get(0);
        List<Patient> patients = patientRepository.findByChunkServerIdAndChunkId(thisServer.getServerId(), chunk.getChunkId());
        List<Visit> visits = visitRepository.findByChunkServerIdAndChunkChunkId(thisServer.getServerId(), chunk.getChunkId());
        List<Measure> measuresToBeProcessed = measureRepository.findAllByJobId(pendingJob.getJobId());
        measureProcessor.initProcessor(measuresToBeProcessed, patients, visits);
        measureProcessor.process();
    }

}
