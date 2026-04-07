package vn.edu.ou.rabbitmq.listener;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import vn.edu.ou.rabbitmq.config.LabRabbitConfigs;
import vn.edu.ou.rabbitmq.dto.TaskPayload;

@Component
public class TaskWorkerListener {

    private static final Logger log = LoggerFactory.getLogger(TaskWorkerListener.class);
    private final ObjectMapper objectMapper;

    public TaskWorkerListener(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @RabbitListener(
            queues = LabRabbitConfigs.Q_TASK_QUEUE,
            ackMode = "MANUAL",
            concurrency = "1")
    public void onTask(String body,
                       Channel channel,
                       @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag)
            throws IOException, InterruptedException {
        TaskPayload task = objectMapper.readValue(body, TaskPayload.class);
        String workerId = String.format(Locale.ROOT, "spring-%d", Thread.currentThread().getId());
        log.info("[Worker {}] Processing task id={} type={}", workerId, task.id(), task.type());

        long processingMs = ThreadLocalRandom.current().nextLong(200, 3000);
        Thread.sleep(processingMs);

        log.info("[Worker {}] Completed task id={}", workerId, task.id());
        channel.basicAck(deliveryTag, false);
    }
}
