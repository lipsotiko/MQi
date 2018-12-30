package io.egia.mqi.chunk;

import io.egia.mqi.job.Job;
import io.egia.mqi.job.JobRepo;
import io.egia.mqi.patient.PatientRecordCount;
import io.egia.mqi.patient.PatientRecordCountRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static io.egia.mqi.chunk.ChunkStatus.PENDING;

@Service
public class ChunkService {
    private Logger log = LoggerFactory.getLogger(ChunkService.class);
    private ChunkRepo chunkRepo;
    private JobRepo jobRepo;
    private PatientRecordCountRepo patientRecordCountRepo;

    @Value("${mqi.properties.system.pageSize}")
    private int pageSize;

    ChunkService(ChunkRepo chunkRepo,
                 JobRepo jobRepo,
                 PatientRecordCountRepo patientRecordCountRepo) {
        this.chunkRepo = chunkRepo;
        this.jobRepo = jobRepo;
        this.patientRecordCountRepo = patientRecordCountRepo;
    }

    public void chunkData(Job job) {
        log.info("Started chunking process");
        chunkRepo.deleteAllInBatch();

        long count = patientRecordCountRepo.count();
        double pages = Math.ceil((float) count / pageSize);

        job.setInitialPatientCount(count);
        jobRepo.saveAndFlush(job);

        for (int i = 0; i < pages; i++) {
            int currentPatient = 0;
            List<PatientRecordCount> patientRecordCounts =
                    patientRecordCountRepo.findBy(PageRequest.of(i, pageSize));
            List<Chunk> chunks = new ArrayList<>();

            while (currentPatient < patientRecordCounts.size()) {

                if (patientRecordCounts.size() == currentPatient) break;

                PatientRecordCount p = patientRecordCounts.get(currentPatient);

                chunks.add(Chunk.builder().patientId(p.getPatientId())
                        .chunkGroup(i)
                        .chunkStatus(PENDING)
                        .recordCount(p.getRecordCount()).build());

                currentPatient++;

            }
            chunkRepo.saveAll(chunks);
        }

        log.info("Completed chunking process");
    }

}
