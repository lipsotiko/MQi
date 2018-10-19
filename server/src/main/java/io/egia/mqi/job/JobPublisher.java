package io.egia.mqi.job;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.observables.ConnectableObservable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class JobPublisher {

    private final Flowable<Job> publisher;

    private JobQueue jobQueue = JobQueue.getStreamInstance();

    public JobPublisher() {
        Observable<Job> helloWorldObservable = Observable.create(emitter -> {
            ScheduledExecutorService executorService = Executors.newScheduledThreadPool(5);
            executorService.scheduleAtFixedRate(newJobs(emitter), 0, 1, TimeUnit.SECONDS);
        });

        ConnectableObservable<Job> connectableObservable = helloWorldObservable.share().publish();
        connectableObservable.connect();

        publisher = connectableObservable.toFlowable(BackpressureStrategy.BUFFER);
    }

    private Runnable newJobs(ObservableEmitter<Job> emitter) {
        return () -> {
            List<Job> updatedJobs = getUpdates();
            emit(emitter, updatedJobs);
        };
    }

    private void emit(ObservableEmitter<Job> emitter, List<Job> helloWorldUpdates) {
        for (Job helloWorld : helloWorldUpdates) {
            try {
                emitter.onNext(helloWorld);
            } catch (RuntimeException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    Flowable<Job> getPublisher() {
        return publisher;
    }

    private List<Job> getUpdates() {
        List<Job> updates = new ArrayList<>();

        while (!jobQueue.isEmpty()) {
            updates.add(jobQueue.poll());
        }

        if (updates.size() == 0) {
            return Collections.emptyList();
        }

        return updates;
    }

}