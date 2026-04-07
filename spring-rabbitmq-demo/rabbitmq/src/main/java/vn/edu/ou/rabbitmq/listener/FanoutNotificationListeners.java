package vn.edu.ou.rabbitmq.listener;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class FanoutNotificationListeners {

    private static final Logger log = LoggerFactory.getLogger(FanoutNotificationListeners.class);
    private final ObjectMapper objectMapper;

    public FanoutNotificationListeners(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "#{@fanoutQueueAlpha.name}", ackMode = "AUTO")
    public void onAlpha(String json) {
        logReceived("Consumer-A", json);
    }

    @RabbitListener(queues = "#{@fanoutQueueBeta.name}", ackMode = "AUTO")
    public void onBeta(String json) {
        logReceived("Consumer-B", json);
    }

    private void logReceived(String name, String json) {
        try {
            JsonNode node = objectMapper.readTree(json);
            log.info("[Fanout {}] Received: type={}, message={}, timestamp={} ",
                    name,
                    node.path("type").asText(),
                    node.path("message").asText(),
                    node.path("timestamp").asText());
        } catch (Exception e) {
            log.warn("[Fanout {}] Raw: {}", name, json);
        }
    }
}
