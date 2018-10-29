package io.egia.mqi;

import io.egia.mqi.chunk.ChunkRepo;
import io.egia.mqi.job.JobMeasureRepo;
import io.egia.mqi.job.JobRepo;
import io.egia.mqi.measure.MeasureResultRepo;
import io.egia.mqi.patient.Patient;
import io.egia.mqi.patient.PatientMeasureLogRepo;
import io.egia.mqi.patient.PatientRepo;
import io.egia.mqi.server.ServerRepo;
import io.egia.mqi.server.ServerService;
import io.egia.mqi.visit.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

@RestController
public class SeedDdController {

    @Autowired private PatientRepo patientRepo;
    @Autowired private VisitRepo visitRepo;
    @Autowired private VisitCodeRepo visitCodeRepo;
    @Autowired private ChunkRepo chunkRepo;
    @Autowired private ServerRepo serverRepo;
    @Autowired private ServerService serverService;
    @Autowired private PatientMeasureLogRepo patientMeasureLogRepo;
    @Autowired private CodeSetRepo codeSetRepo;
    @Autowired private CodeSetGroupRepo codeSetGroupRepo;
    @Autowired private MeasureResultRepo measureResultRepo;
    @Autowired private JobRepo jobRepo;
    @Autowired private JobMeasureRepo jobMeasureRepo;


    @GetMapping("/seed")
    public Map<String, Integer> seedDb() {

        chunkRepo.deleteAll();
        visitCodeRepo.deleteAll();
        visitRepo.deleteAll();
        patientRepo.deleteAll();
        codeSetRepo.deleteAll();
        codeSetGroupRepo.deleteAll();
        patientMeasureLogRepo.deleteAll();
        measureResultRepo.deleteAll();
        jobMeasureRepo.deleteAll();
        jobRepo.deleteAll();

        for (long i = 1L; i <= 50; i++) {
            Patient patient = new Patient();
            patient.setFirstName("Vango");
            patient.setLastName("Laouto");
            patient.setGender('M');
            patient.setDateOfBirth(new GregorianCalendar(1956, Calendar.APRIL, 28).getTime());
            Patient savedPatient = patientRepo.saveAndFlush(patient);

            Visit visit = new Visit();
            visit.setPatientId(savedPatient.getPatientId());
            Visit savedVisit = visitRepo.saveAndFlush(visit);

            VisitCode code_1 = new VisitCode();
            code_1.setVisitId(savedVisit.getVisitId());
            code_1.setCodeValue("xyz");
            code_1.setCodeSystem(CodeSystem.ICD_9);
            visitCodeRepo.saveAndFlush(code_1);

            VisitCode code_2 = new VisitCode();
            code_2.setVisitId(savedVisit.getVisitId());
            code_2.setCodeValue("abc.defgh");
            code_2.setCodeSystem(CodeSystem.ICD_10);
            visitCodeRepo.saveAndFlush(code_2);

            VisitCode code_3 = new VisitCode();
            code_3.setVisitId(savedVisit.getVisitId());
            code_3.setCodeValue("99");
            code_3.setCodeSystem(CodeSystem.POS);
            visitCodeRepo.saveAndFlush(code_3);

            VisitCode code_4 = new VisitCode();
            code_4.setVisitId(savedVisit.getVisitId());
            code_4.setCodeValue("22");
            code_4.setCodeSystem(CodeSystem.REV);
            visitCodeRepo.saveAndFlush(code_4);
        }

        CodeSetGroup codeSetA = codeSetGroupRepo.save(CodeSetGroup.builder().groupName("CODE_SET_A").build());
        CodeSetGroup codeSetB = codeSetGroupRepo.save(CodeSetGroup.builder().groupName("CODE_SET_B").build());

        codeSetRepo.save(buildCodeSet(codeSetA, CodeSystem.POS, "99"));
        codeSetRepo.save(buildCodeSet(codeSetA, CodeSystem.REV, "22"));
        codeSetRepo.save(buildCodeSet(codeSetB, CodeSystem.ICD_9, "xyz"));
        codeSetRepo.save(buildCodeSet(codeSetB, CodeSystem.ICD_10, "abc.defgh"));

        Map<String, Integer> results = new HashMap<>();
        results.put("Code Set Groups:", codeSetGroupRepo.findAll().size());
        results.put("Code Set Codes:", codeSetRepo.findAll().size());
        results.put("Visit Codes:", visitCodeRepo.findAll().size());
        results.put("Visits:", visitRepo.findAll().size());
        results.put("Patients:", patientRepo.findAll().size());
        results.put("Servers:", serverRepo.findAll().size());
        return results;
    }

    private CodeSet buildCodeSet(CodeSetGroup codeSetGroup, CodeSystem codeSystem, String codeValue) {
        return CodeSet.builder().codeSetGroup(codeSetGroup).codeSystem(codeSystem).codeValue(codeValue).build();
    }
}
