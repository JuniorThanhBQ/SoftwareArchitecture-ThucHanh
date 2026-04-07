package vn.edu.ou.rabbitmq.listener;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class TopicOrderListener {

    private static final Logger log = LoggerFactory.getLogger(TopicOrderListener.class);
    private final ObjectMapper objectMapper;

    public TopicOrderListener(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "#{@topicOrdersQueue.name}", ackMode = "AUTO")
    public void onTopic(String json,
                        @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String routingKey) {
        try {
            JsonNode node = objectMapper.readTree(json);
            log.info("[Topic] routingKey={} payload={}", routingKey, node.toString());
        } catch (Exception e) {
            log.warn("[Topic] routingKey={} raw={}", routingKey, json);
        }
    }
}
