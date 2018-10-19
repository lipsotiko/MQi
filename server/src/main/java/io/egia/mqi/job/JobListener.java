package io.egia.mqi.job;

import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;

public class JobListener {

    private JobQueue jobQueue = JobQueue.getStreamInstance();

    @PostPersist
    public void addSavedJobToQueue(Job job) {
        jobQueue.add(job);
    }

    @PostUpdate
    public void addUpdatedJobToQueue(Job job) {
        jobQueue.add(job);
    }
}
