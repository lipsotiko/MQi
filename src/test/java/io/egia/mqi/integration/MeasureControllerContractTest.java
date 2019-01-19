package io.egia.mqi.integration;

import io.egia.mqi.job.Job;
import io.egia.mqi.job.JobRepo;
import io.egia.mqi.measure.Measure;
import io.egia.mqi.measure.MeasureController;
import io.egia.mqi.measure.MeasureListItem;
import io.egia.mqi.measure.MeasureRepo;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import static io.egia.mqi.job.JobStatus.DONE;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class MeasureControllerContractTest {

    @Autowired
    private MeasureRepo measureRepo;

    @Autowired
    private JobRepo jobRepo;

    @Autowired
    private MeasureController measureController;

    @Test
    public void measures_are_returned_with_a_status() {
        Measure measure = new Measure();
        measure.setLastUpdated(ZonedDateTime.now());
        Measure savedMeasure = measureRepo.save(measure);
        jobRepo.save(Job.builder()
                .measureIds(Collections.singletonList(savedMeasure.getMeasureId()))
                .lastUpdated(ZonedDateTime.now())
                .jobStatus(DONE).build());
        List<MeasureListItem> measureList = measureController.getMeasureList();
        assertThat(measureList.get(0).getJobStatus()).isEqualTo(DONE);
    }

    @Test
    public void measures_are_returned_with_no_status_when_updated_after_last_job() {
        Measure measure = new Measure();
        measure.setLastUpdated(ZonedDateTime.now().plusDays(1L));
        Measure savedMeasure = measureRepo.save(measure);
        jobRepo.save(Job.builder()
                .measureIds(Collections.singletonList(savedMeasure.getMeasureId()))
                .jobStatus(DONE)
                .lastUpdated(ZonedDateTime.now())
                .build());
        List<MeasureListItem> measureList = measureController.getMeasureList();
        assertThat(measureList.get(0).getJobStatus()).isEqualTo(null);
    }

    @After
    public void setUp() {
        measureRepo.deleteAll();
        jobRepo.deleteAll();
    }
}
