package io.egia.mqi.chunk;

import io.egia.mqi.measure.Measure;
import io.egia.mqi.patient.PatientMeasureLog;
import io.egia.mqi.patient.PatientMeasureLogRepository;
import io.egia.mqi.patient.PatientRecordCount;
import io.egia.mqi.patient.PatientRecordCountRepository;
import io.egia.mqi.server.Server;
import io.egia.mqi.server.ServerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChunkService {
    private Logger log = LoggerFactory.getLogger(ChunkService.class);
    private ChunkRepository chunkRepository;
    private PatientRecordCountRepository patientRecordCountRepository;
    private PatientMeasureLogRepository patientMeasureLogRepository;
    private ServerRepository serverRepository;

    public ChunkService(ServerRepository serverRepository,
                        ChunkRepository chunkRepository,
                        PatientRecordCountRepository patientRecordCountRepository,
                        PatientMeasureLogRepository patientMeasureLogRepository) {
        this.serverRepository = serverRepository;
        this.chunkRepository = chunkRepository;
        this.patientRecordCountRepository = patientRecordCountRepository;
        this.patientMeasureLogRepository = patientMeasureLogRepository;
    }

    public void chunkData(List<Measure> measures) {
        List<Server> servers = serverRepository.findAll();
        List<PatientRecordCount> patientRecordCounts = patientRecordCountRepository.findAll();
        List<PatientRecordCount> patientsToProcess = filterProcessedPatients(patientRecordCounts, measures);

        do {
            List<Chunk> chunks = new ArrayList<>();

            int currentPatient = 0;
            while (currentPatient < patientsToProcess.size()) {
                for (Server s : servers) {

                    if (patientsToProcess.size() == currentPatient) {
                        break;
                    }

                    PatientRecordCount p = patientsToProcess.get(currentPatient);

                    chunks.add(Chunk.builder().patientId(p.getPatientId())
                            .serverId(s.getServerId())
                            .chunkStatus(ChunkStatus.PENDING)
                            .recordCount(p.getRecordCount()).build());

                    currentPatient++;
                }
            }

            chunkRepository.saveAll(chunks);
            patientRecordCounts = patientRecordCountRepository.findAll();
            patientsToProcess = filterProcessedPatients(patientRecordCounts, measures);

        } while (patientsToProcess.size() > 0);

        log.info("Executing chunking process");
    }

    private List<PatientRecordCount> filterProcessedPatients(List<PatientRecordCount> patientRecordCounts, List<Measure> measures) {
        List<Long> patientIds =
                patientRecordCounts.stream().map(PatientRecordCount::getPatientId).collect(Collectors.toList());
        List<PatientMeasureLog> patientMeasureLogs = patientMeasureLogRepository.findAllById(patientIds);
        return patientRecordCounts.stream().filter(p -> processPatient(p, patientMeasureLogs, measures)).collect(Collectors.toList());
    }

    private Boolean processPatient(PatientRecordCount patient, List<PatientMeasureLog> log, List<Measure> measures) {
        List<PatientMeasureLog> patientLog =
                log.stream().filter(p -> p.getPatientId().equals(patient.getPatientId())).collect(Collectors.toList());

        for (Measure m : measures) {
            for (PatientMeasureLog p : patientLog) {
                if (p.getMeasureId().equals(m.getMeasureId())
                        && m.getLastUpdated().isAfter(p.getLastUpdated())) {
                    return false;
                }
            }
        }

        return true;
    }

}
