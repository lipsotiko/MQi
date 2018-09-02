package io.egia.mqi.integration;

import io.egia.mqi.chunk.Chunk;
import io.egia.mqi.chunk.ChunkRepository;
import io.egia.mqi.patient.Patient;
import io.egia.mqi.patient.PatientRepository;
import io.egia.mqi.server.Server;
import io.egia.mqi.server.ServerRepository;
import io.egia.mqi.visit.DxCodeRepository;
import io.egia.mqi.visit.Visit;
import io.egia.mqi.visit.VisitDxCode;
import io.egia.mqi.visit.VisitRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;
import java.util.List;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class PatientAndVisitRepositoriesIntegrationTest {

    @Autowired private ServerRepository serverRepository;
    @Autowired private ChunkRepository chunkRepository;
    @Autowired private PatientRepository patientRepository;
    @Autowired private VisitRepository visitRepository;
    @Autowired private DxCodeRepository dxCodeRepository;

    @Before
    public void setUp() {
        Server server = Server.builder().serverId(1L).build();
        serverRepository.save(server);

        Chunk chunk = new Chunk();
        chunk.setPatientId(1L);
        chunk.setRecordCnt(100L);
        chunk.setServerId(1L);
        chunk.setChunkId(1L);
        chunkRepository.save(chunk);

        Patient patient = new Patient();
        patient.setChunk(chunk);
        patient.setPatientId(1L);
        patientRepository.save(patient);

        VisitDxCode visitDxCode = new VisitDxCode();
        visitDxCode.setVisitId(1L);
        visitDxCode.setDxCode("123.12345");
        dxCodeRepository.save(visitDxCode);

        Visit visit = new Visit();
        visit.setPatientId(1L);
        visit.setVisitId(1L);
        visit.setChunk(chunk);
        visit.setVisitDxCodes(Collections.singletonList(visitDxCode));
        visitRepository.save(visit);
    }

    @Test
    public void patientRepository_findByServerIdAndChunkId() {
        patientRepository.findByServerIdAndChunkId(1L, 1L);
    }

    @Test
    public void visitRepository_findByServerIdAndChunkId() {
        List<Visit> byServerIdAndChunkId = visitRepository.findByServerIdAndChunkId(1L, 1L);
//        Visit visit = byServerIdAndChunkId.get(0);
//        List<VisitDxCode> visitDxCodes = visit.getVisitDxCodes();
//        VisitCode visitCode = visitCodes.get(0);
//        visitCode.getCodeType();
//        visitCode.getCode();
    }

}
