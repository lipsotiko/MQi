package io.egia.mqi.integration;

import io.egia.mqi.visit.Visit;
import io.egia.mqi.visit.VisitRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class VisitRepositoryIntegrationTest extends AbstractRepositoryTest {

    @Autowired
    private VisitRepository visitRepository;

    @Test
    public void visitRepository_findByServerIdAndChunkId() {
        List<Visit> subject = visitRepository.findByChunkServerIdAndChunkChunkId(1L, 1L);
        assertThat(subject.size()).isEqualTo(1);
        assertThat(subject.get(0).getVisitCodes().get(0).getCodeSystem()).isEqualTo("ICD_9");
        assertThat(subject.get(0).getVisitCodes().get(0).getCodeValue()).isEqualTo("abc");
    }

}
