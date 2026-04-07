package vn.edu.ou.rabbitmq.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class DirectLogListener {

    private static final Logger log = LoggerFactory.getLogger(DirectLogListener.class);

    @RabbitListener(queues = "#{@directLogsQueue.name}", ackMode = "AUTO")
    public void onDirectLog(String message) {
        log.info("[Direct] Received: {}", message);
    }
}
