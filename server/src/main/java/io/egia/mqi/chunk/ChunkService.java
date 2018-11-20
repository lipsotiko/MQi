package io.egia.mqi.chunk;

import io.egia.mqi.job.Job;
import io.egia.mqi.job.JobRepo;
import io.egia.mqi.patient.PatientRecordCount;
import io.egia.mqi.patient.PatientRecordCountRepo;
import io.egia.mqi.server.Server;
import io.egia.mqi.server.ServerRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static io.egia.mqi.chunk.ChunkStatus.PENDING;
import static io.egia.mqi.chunk.ChunkStatus.PROCESSED;
import static io.egia.mqi.server.SystemType.PRIMARY;

@Service
public class ChunkService {
    private Logger log = LoggerFactory.getLogger(ChunkService.class);
    private ChunkRepo chunkRepo;
    private JobRepo jobRepo;
    private PatientRecordCountRepo patientRecordCountRepo;
    private ServerRepo serverRepo;

    ChunkService(ServerRepo serverRepo,
                 ChunkRepo chunkRepo,
                 JobRepo jobRepo,
                 PatientRecordCountRepo patientRecordCountRepo) {
        this.serverRepo = serverRepo;
        this.chunkRepo = chunkRepo;
        this.jobRepo = jobRepo;
        this.patientRecordCountRepo = patientRecordCountRepo;
    }

    public void chunkData(Job job) {
        log.info("Started chunking process");
        chunkRepo.deleteAllInBatch();
        List<Server> servers = serverRepo.findAll();

        long count = patientRecordCountRepo.count();
        int pageSize = getPageSize(servers);
        double pages = Math.ceil((float)count / pageSize);

        job.setInitialPatientCount(count);
        jobRepo.save(job);

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
                            .chunkStatus(PENDING)
                            .recordCount(p.getRecordCount()).build());

                    currentPatient++;
                }
            }
            chunkRepo.saveAll(chunks);
        }

        log.info("Completed chunking process");
    }

    private int getPageSize(List<Server> servers) {
        return servers.stream()
                .filter(s -> s.getSystemType().equals(PRIMARY))
                .collect(Collectors.toList())
                .get(0)
                .getPageSize();
    }

}
