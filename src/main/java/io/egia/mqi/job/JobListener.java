package io.egia.mqi.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;

@Component
public class JobListener {

    static private SimpMessagingTemplate template;

    @PostPersist
    @PostUpdate
    public void emit(Job job) {
        template.convertAndSend("/topic/job", job);
    }

    @Autowired
    public void init(SimpMessagingTemplate template) {
        JobListener.template = template;
    }
}
