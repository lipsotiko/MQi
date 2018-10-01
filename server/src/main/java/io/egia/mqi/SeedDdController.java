package io.egia.mqi;

import io.egia.mqi.chunk.ChunkRepository;
import io.egia.mqi.patient.Patient;
import io.egia.mqi.patient.PatientMeasureLogRepository;
import io.egia.mqi.patient.PatientRepository;
import io.egia.mqi.server.Server;
import io.egia.mqi.server.ServerRepository;
import io.egia.mqi.server.ServerService;
import io.egia.mqi.server.SystemType;
import io.egia.mqi.visit.Visit;
import io.egia.mqi.visit.VisitCode;
import io.egia.mqi.visit.VisitCodeRepository;
import io.egia.mqi.visit.VisitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class SeedDdController {

    @Autowired private PatientRepository patientRepository;
    @Autowired private VisitRepository visitRepository;
    @Autowired private VisitCodeRepository visitCodeRepository;
    @Autowired private ChunkRepository chunkRepository;
    @Autowired private ServerRepository serverRepository;
    @Autowired private ServerService serverService;
    @Autowired private PatientMeasureLogRepository patientMeasureLogRepository;

    @GetMapping("/seed")
    public Map<String, Integer> seedDb() throws UnknownHostException {

        chunkRepository.deleteAll();
        visitCodeRepository.deleteAll();
        visitRepository.deleteAll();
        patientRepository.deleteAll();
        serverRepository.deleteAll();
        patientMeasureLogRepository.deleteAll();

        serverRepository.saveAndFlush(
                Server.builder().serverId(1L)
                        .serverName(InetAddress.getLocalHost().getHostName())
                        .systemType(SystemType.PRIMARY)
                        .systemVersion("1.0.0")
                        .serverPort("8080").build());

        serverRepository.saveAndFlush(
                Server.builder().serverId(2L)
                        .serverName("Test Server 2")
                        .systemType(SystemType.SECONDARY)
                        .systemVersion("1.0.0")
                        .serverPort("8081").build());

        serverRepository.saveAndFlush(
                Server.builder().serverId(3L)
                        .serverName("Test Server 3")
                        .systemType(SystemType.SECONDARY)
                        .systemVersion("1.0.0")
                        .serverPort("8082").build());

        for (Long i = 1L; i <= 150; i++) {
            Patient patient = new Patient();
            patient.setFirstName("Vango");
            patient.setLastName("Laouto");
            patient.setGender('M');
            Patient savedPatient = patientRepository.saveAndFlush(patient);

            Visit visit = new Visit();
            visit.setPatientId(savedPatient.getPatientId());
            Visit savedVisit = visitRepository.saveAndFlush(visit);

            for(Long j = 1L; j < 9L; j++) {
                VisitCode code = new VisitCode();
                code.setVisitId(savedVisit.getVisitId());
                code.setCodeValue("abc");
                code.setCodeSystem("ICD_9");
                visitCodeRepository.saveAndFlush(code);
            }

        }

        Map<String, Integer> results = new HashMap<>();
        results.put("Patients:", patientRepository.findAll().size());
        results.put("Visits:", visitRepository.findAll().size());
        results.put("Visit Codes:", visitCodeRepository.findAll().size());
        results.put("Servers:", serverRepository.findAll().size());
        return results;
    }

}
