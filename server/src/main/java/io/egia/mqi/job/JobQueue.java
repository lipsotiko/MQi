package io.egia.mqi.job;

import java.util.LinkedList;
import java.util.Queue;

class JobQueue {

    private static final Queue<Job> queue = new LinkedList<>();
    private static JobQueue queueInstance = null;

    static JobQueue getStreamInstance() {
        if (queueInstance == null) {
            queueInstance = new JobQueue();
        }
        return queueInstance;
    }

    void add(Job job) {
        synchronized (queue) {
            queue.add(job);
        }
    }

    Job poll() {
        return queue.poll();
    }

    boolean isEmpty() {
        return queue.isEmpty();
    }

}
