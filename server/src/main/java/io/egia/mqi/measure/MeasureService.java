package io.egia.mqi.measure;

import io.egia.mqi.chunk.ChunkRepository;
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

    public MeasureService(MeasureRepository measureRepository
            , ChunkRepository chunkRepository
            , JobRepository jobRepository
            , ServerService serverService
            , PatientRepository patientRepository
            , VisitRepository visitRepository) {
        this.measureRepository = measureRepository;
        this.chunkRepository = chunkRepository;
        this.jobRepository = jobRepository;
        this.serverService = serverService;
        this.patientRepository = patientRepository;
        this.visitRepository = visitRepository;
    }

    @Value("${server.port}")
    private String serverPort;

    MeasureProcessor measureProcessor;

    public void measureProcess() {
        Long jobId;
        Long chunkId;

        try {
            Server thisServer = serverService.getServerFromHostNameAndPort(serverPort);
            Long serverId = thisServer.getServerId();
            jobId = jobRepository.findByStatusOrderByOrderIdAsc("pending").get(0).getJobId();

            log.info(String.format("Processing job(Id): %s, on server(Id):%s", jobId, serverId));
            jobRepository.updateJobStatus(jobId, "running");
            chunkId = chunkRepository.findOneByServerIdOrderByChunkIdAsc(serverId).getChunkId();

            log.info(String.format("Populating chunk id: %s into measure processor.", chunkId));
            List<Patient> patients = patientRepository.findByServerIdAndChunkId(serverId, chunkId);
            List<Visit> visits = visitRepository.findByServerIdAndChunkId(serverId, chunkId);

            log.info(String.format("Setting measures in measure processor.", chunkId));
            List<Measure> measures = measureRepository.findAllByJobId(jobId);
            measureProcessor = new MeasureProcessor(chunkId, measures, patients, visits);

            measureProcessor.process();
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
