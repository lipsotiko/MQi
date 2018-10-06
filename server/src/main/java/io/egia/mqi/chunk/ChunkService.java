package io.egia.mqi.chunk;

import io.egia.mqi.patient.PatientRecordCount;
import io.egia.mqi.patient.PatientRecordCountRepo;
import io.egia.mqi.server.Server;
import io.egia.mqi.server.ServerRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChunkService {
    private Logger log = LoggerFactory.getLogger(ChunkService.class);
    private ChunkRepo chunkRepo;
    private PatientRecordCountRepo patientRecordCountRepo;
    private ServerRepo serverRepo;

    public ChunkService(ServerRepo serverRepo,
                        ChunkRepo chunkRepo,
                        PatientRecordCountRepo patientRecordCountRepo) {
        this.serverRepo = serverRepo;
        this.chunkRepo = chunkRepo;
        this.patientRecordCountRepo = patientRecordCountRepo; }

    public void chunkData() {
        chunkRepo.deleteAllInBatch();
        List<Server> servers = serverRepo.findAll();
        List<PatientRecordCount> patientRecordCounts = patientRecordCountRepo.findTop5000By();
        int chunkGroup = 1;

        do {
            List<Chunk> chunks = new ArrayList<>();

            int currentPatient = 0;
            while (currentPatient < patientRecordCounts.size()) {
                for (Server s : servers) {

                    if (patientRecordCounts.size() == currentPatient) {
                        break;
                    }

                    PatientRecordCount p = patientRecordCounts.get(currentPatient);

                    chunks.add(Chunk.builder().patientId(p.getPatientId())
                            .serverId(s.getServerId())
                            .chunkGroup(chunkGroup)
                            .chunkStatus(ChunkStatus.PENDING)
                            .recordCount(p.getRecordCount()).build());

                    currentPatient++;
                }
            }

            chunkRepo.saveAll(chunks);
            patientRecordCounts = patientRecordCountRepo.findTop5000By();
            chunkGroup++;
        } while (patientRecordCounts.size() > 0);

        log.info("Executing chunking process");
    }

}
