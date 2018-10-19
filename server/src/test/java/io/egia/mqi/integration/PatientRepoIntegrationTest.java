package io.egia.mqi.integration;

import io.egia.mqi.patient.Patient;
import io.egia.mqi.patient.PatientRepo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PatientRepoIntegrationTest extends AbstractRepositoryTest {

    @Autowired
    private PatientRepo patientRepo;

    @Test
    public void patientRepo_findByServerIdAndChunkGroup() {
        List<Patient> subject = patientRepo.findByServerIdAndChunkGroup(1L,0);
        assertThat(subject.size()).isEqualTo(1);
    }

}
