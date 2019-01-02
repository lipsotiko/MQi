package io.egia.mqi.integration;

import io.egia.mqi.visit.CodeSet;
import io.egia.mqi.visit.CodeSetGroup;
import io.egia.mqi.visit.CodeSetGroupRepo;
import io.egia.mqi.visit.CodeSetRepo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class CodeSetRepoTest {

    @Autowired private CodeSetGroupRepo codeSetGroupRepo;
    @Autowired private CodeSetRepo codeSetRepo;

    @Test
    public void codeSetRepo_findByCodeSetGroupIdIn() {
        CodeSetGroup csg1 = CodeSetGroup.builder().groupName("vango1").build();
        codeSetGroupRepo.save(csg1);
        codeSetRepo.save(CodeSet.builder().codeSetGroup(csg1).build());

        CodeSetGroup csg2 = CodeSetGroup.builder().groupName("vango2").build();
        codeSetGroupRepo.save(csg2);
        codeSetRepo.save(CodeSet.builder().codeSetGroup(csg2).build());

        Set<UUID> codeSetGroupIds = new HashSet<UUID>(){{
            add(csg1.getId());
            add(csg2.getId());
        }};

        List<CodeSet> byCodeSetGroupId = codeSetRepo.findByCodeSetGroupIdIn(codeSetGroupIds);
        assertThat(byCodeSetGroupId.size()).isEqualTo(2);
    }
}