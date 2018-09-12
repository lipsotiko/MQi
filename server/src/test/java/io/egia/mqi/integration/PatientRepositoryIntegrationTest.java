package io.egia.mqi.integration;

import io.egia.mqi.patient.Patient;
import io.egia.mqi.patient.PatientRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PatientRepositoryIntegrationTest extends AbstractRepositoryTest {

    @Autowired
    private PatientRepository patientRepository;

    @Test
    public void patientRepository_findByServerIdAndChunkGroup() {
        List<Patient> subject = patientRepository.findByServerIdAndChunkGroup(1L, 1L);
        assertThat(subject.size()).isEqualTo(1);
    }

}
