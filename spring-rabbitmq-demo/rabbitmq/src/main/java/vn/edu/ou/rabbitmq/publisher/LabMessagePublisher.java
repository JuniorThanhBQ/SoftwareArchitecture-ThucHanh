package vn.edu.ou.rabbitmq.publisher;

import java.time.Instant;
import java.util.Map;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import vn.edu.ou.rabbitmq.config.LabRabbitConfigs;
import vn.edu.ou.rabbitmq.dto.NotificationPayload;
import vn.edu.ou.rabbitmq.dto.TaskPayload;

@Service
public class LabMessagePublisher {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public LabMessagePublisher(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    public void publishDirect(String severity, String body) {
        rabbitTemplate.convertAndSend(LabRabbitConfigs.EX_DIRECT_LOGS, severity, body);
    }

    public void publishAllDirectSeverities() {
        for (String severity : new String[]{"info", "warning", "error"}) {
            String message = "[" + severity.toUpperCase() + "] Message at " + Instant.now();
            publishDirect(severity, message);
        }
    }

    public void publishFanout(NotificationPayload payload) throws JacksonException {
        String json = objectMapper.writeValueAsString(payload);
        rabbitTemplate.convertAndSend(LabRabbitConfigs.EX_NOTIFICATIONS, "", json);
    }

    public void publishFanoutDefaultMaintenance() throws JacksonException {
        publishFanout(new NotificationPayload(
                "notification",
                "System maintenance at 10:00 PM",
                Instant.now()));
    }

    public void publishTopic(String routingKey, Map<String, Object> body) throws JacksonException {
        String json = objectMapper.writeValueAsString(body);
        rabbitTemplate.convertAndSend(LabRabbitConfigs.EX_TOPIC_ORDERS, routingKey, json);
    }

    public void sendTask(TaskPayload task) throws JacksonException {
        String json = objectMapper.writeValueAsString(task);
        rabbitTemplate.convertAndSend("", LabRabbitConfigs.Q_TASK_QUEUE, json, message -> {
            message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            return message;
        });
    }

}
