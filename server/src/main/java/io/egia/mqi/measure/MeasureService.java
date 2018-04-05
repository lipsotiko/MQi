package io.egia.mqi.measure;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import io.egia.mqi.chunk.ChunkRepository;
import io.egia.mqi.job.JobRepository;
import io.egia.mqi.patient.Patient;
import io.egia.mqi.patient.PatientRepository;
import io.egia.mqi.server.Server;
import io.egia.mqi.server.ServerRepository;
import io.egia.mqi.visit.Visit;
import io.egia.mqi.visit.VisitRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/*
 *
 * @author vango
 *
 *		The purpose of this class is to
 *			-return measures to the front-end
 *			-process a measure/patient combination
 *
 */

@Service
public class MeasureService {
    private Logger log = LoggerFactory.getLogger(MeasureService.class);

    @Autowired
    private MeasureRepository measureRepository;

    @Autowired
    private ChunkRepository chunkRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ServerRepository serverRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private VisitRepository visitRepository;

    @Value("${server.port}")
    private String serverPort;

    MeasureWorkspace measureWorkspace;

    public List<Measure> getMeasure() {
        return measureRepository.findAll();
    }

    public List<Measure> getMeasure(Long measureId) {
        return measureRepository.findByMeasureId(measureId);
    }

    public void measureProcess() throws InterruptedException {
        Long jobId;
        Long chunkId;
        String serverName;
        InetAddress serverIp;

        //This process needs to be refined to work across several servers
        try {
            serverIp = InetAddress.getLocalHost();
            serverName = serverIp.getHostName();

            List<Server> thisServer = serverRepository.findByServerNameAndServerPort(serverName, serverPort);
            Long serverId = thisServer.get(0).getServerId();

            jobId = jobRepository.findByStatusOrderByOrderIdAsc("pending").get(0).getJobId();
            log.info(String.format("Processing job(Id): %s, on server(Id):%s", jobId, serverId));

            jobRepository.updateJobStatus(jobId, "running");

            //Retrieve the first chunk to be processed
            chunkId = chunkRepository.findTop1ByServerIdOrderByChunkIdAsc(serverId).get(0).getChunkId();

            //This will be put into a loop until there are no more chunks available
            if (chunkId != null) {

                log.info(String.format("Populating chunk id: %s into measure workspace.", chunkId));
                List<Patient> patients = patientRepository.findByServerIdAndChunkId(serverId, chunkId);
                List<Visit> visits = visitRepository.findByServerIdAndChunkId(serverId, chunkId);
                measureWorkspace = new MeasureWorkspace(patients, visits);

            } else {
                log.info("No chunks found. Exiting measure service.");
            }

            //log.info("Retrieve measures from job.");
            //List<measure> measure = measureRepository.findByMeasureId(measureId);
            //Thread.sleep(10000L);
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public Integer getPatientCount() {
        return measureWorkspace.getPatientCount();
    }

    public List<MeasureListItem> getMeasureList() {
        return measureRepository.findAllMeasureListItems();
    }
}
