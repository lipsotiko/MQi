package io.egia.mqi.measure;

import io.egia.mqi.chunk.ChunkRepository;
import io.egia.mqi.job.JobRepository;
import io.egia.mqi.patient.Patient;
import io.egia.mqi.patient.PatientRepository;
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

    public MeasureService(MeasureRepository measureRepository
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

    private List<Patient> patients;
    private List<Visit> visits;
    private Long thisServerId;
    private Rules rules = new Rules();

    public void process() {
        thisServerId = getThisServerId();
        Long jobId = getPendingJobId();
        setJobStatus(jobId, thisServerId, "running");

        Long chunkId = getChunkIdToProcess();
        getPatientData(chunkId, thisServerId);
        measureProcessor.setMeasures(getMeasuresToBeProcessed(jobId, chunkId));
        measureProcessor.setRules(rules);
        measureProcessor.setPatientData(patients, visits);
        measureProcessor.process();
    }

    private Long getChunkIdToProcess() {
        return chunkRepository.findByServerIdOrderByChunkIdAsc(thisServerId).get(0).getChunkId();
    }

    private Long getPendingJobId() {
        return jobRepository.findByStatusOrderByOrderIdAsc("pending").get(0).getJobId();
    }

    private Long getThisServerId() {
        try {
            return serverService.getServerFromHostNameAndPort(serverPort).getServerId();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return -1L;
    }

    private List<Measure> getMeasuresToBeProcessed(Long jobId, Long chunkId) {
        log.info(String.format("Get measures to be processed.", chunkId));
        return measureRepository.findAllByJobId(jobId);
    }

    private void getPatientData(Long chunkId, Long thisServerId) {
        log.info(String.format("Populating chunk id: %s into measure processor.", chunkId));
        patients = patientRepository.findByServerIdAndChunkId(thisServerId, chunkId);
        visits = visitRepository.findByServerIdAndChunkId(thisServerId, chunkId);
    }

    private void setJobStatus(Long jobId, Long serverId, String status) {
        log.info(String.format("Processing job(Id): %s, on server(Id):%s", jobId, serverId));
        jobRepository.updateJobStatus(jobId, status);
    }

}
