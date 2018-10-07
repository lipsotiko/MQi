package io.egia.mqi.chunk;

import io.egia.mqi.patient.PatientRecordCount;
import io.egia.mqi.patient.PatientRecordCountRepo;
import io.egia.mqi.server.Server;
import io.egia.mqi.server.ServerRepo;
import io.egia.mqi.server.SystemType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        this.patientRecordCountRepo = patientRecordCountRepo;
    }

    public void chunkData() {
        log.info("Executing chunking process");
        chunkRepo.deleteAllInBatch();
        List<Server> servers = serverRepo.findAll();

        long count = patientRecordCountRepo.count();
        int pageSize = getPageSize(servers);
        long pages = count / pageSize;

        for (int i = 0; i < pages; i++) {
            int currentPatient = 0;
            List<PatientRecordCount> patientRecordCounts =
                    patientRecordCountRepo.findBy(PageRequest.of(i, pageSize));
            List<Chunk> chunks = new ArrayList<>();

            while (currentPatient < patientRecordCounts.size()) {
                for (Server s : servers) {

                    if (patientRecordCounts.size() == currentPatient) {
                        break;
                    }

                    PatientRecordCount p = patientRecordCounts.get(currentPatient);

                    chunks.add(Chunk.builder().patientId(p.getPatientId())
                            .serverId(s.getServerId())
                            .chunkGroup(i)
                            .chunkStatus(ChunkStatus.PENDING)
                            .recordCount(p.getRecordCount()).build());

                    currentPatient++;
                }
            }
            chunkRepo.saveAll(chunks);
        }
    }

    private int getPageSize(List<Server> servers) {
        return servers.stream()
                .filter(s -> s.getSystemType().equals(SystemType.PRIMARY))
                .collect(Collectors.toList())
                .get(0)
                .getPageSize();
    }

}
