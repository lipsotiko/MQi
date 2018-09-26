package io.egia.mqi.chunk;

import io.egia.mqi.measure.Measure;
import io.egia.mqi.patient.PatientRecordCount;
import io.egia.mqi.patient.PatientRecordCountRepository;
import io.egia.mqi.server.Server;
import io.egia.mqi.server.ServerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChunkService {
    private Logger log = LoggerFactory.getLogger(ChunkService.class);
    private ChunkRepository chunkRepository;
    private PatientRecordCountRepository patientRecordCountRepository;
    private ServerRepository serverRepository;

    public ChunkService(ServerRepository serverRepository,
                        ChunkRepository chunkRepository,
                        PatientRecordCountRepository patientRecordCountRepository) {
        this.serverRepository = serverRepository;
        this.chunkRepository = chunkRepository;
        this.patientRecordCountRepository = patientRecordCountRepository;
    }

    public void chunkData(List<Measure> measures) {
        List<Server> servers = serverRepository.findAll();
        List<PatientRecordCount> patientRecordCounts = patientRecordCountRepository.findAll();

        do {
            List<Chunk> chunks = new ArrayList<>();

            int currentPatient = 0;
            while (currentPatient < patientRecordCounts.size()) {
                for (Server s : servers) {
                    PatientRecordCount p = patientRecordCounts.get(currentPatient);

                    chunks.add(Chunk.builder().patientId(p.getPatientId())
                            .serverId(s.getServerId())
                            .chunkStatus(ChunkStatus.PENDING)
                            .recordCount(p.getRecordCount()).build());

                    currentPatient++;
                }
            }

            chunkRepository.saveAll(chunks);
            patientRecordCounts = patientRecordCountRepository.findAll();

        } while (patientRecordCounts.size() > 0);

        log.info("Executing chunking process");
    }
}
