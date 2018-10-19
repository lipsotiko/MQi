package io.egia.mqi.integration;

import io.egia.mqi.visit.CodeSystem;
import io.egia.mqi.visit.Visit;
import io.egia.mqi.visit.VisitRepo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class VisitRepositoryIntegrationTest extends AbstractRepositoryTest {

    @Autowired
    private VisitRepo visitRepo;

    @Test
    public void visitRepo_findByServerIdAndChunkGroup() {
        List<Visit> subject = visitRepo.findByServerIdAndChunkGroup(1L,0);
        assertThat(subject.size()).isEqualTo(1);
        assertThat(subject.get(0).getVisitCodes().get(0).getCodeSystem()).isEqualTo(CodeSystem.ICD_9);
        assertThat(subject.get(0).getVisitCodes().get(0).getCodeValue()).isEqualTo("abc");
    }
}
