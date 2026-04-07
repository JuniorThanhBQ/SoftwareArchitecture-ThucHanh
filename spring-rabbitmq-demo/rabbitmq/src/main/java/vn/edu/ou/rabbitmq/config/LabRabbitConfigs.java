package vn.edu.ou.rabbitmq.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.amqp.core.AnonymousQueue;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarable;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(LabRabbitProperties.class)
public class LabRabbitConfigs {

    public static final String EX_DIRECT_LOGS = "direct_logs";
    public static final String EX_NOTIFICATIONS = "notifications";
    public static final String EX_TOPIC_ORDERS = "topic_orders";
    public static final String Q_TASK_QUEUE = "task_queue";

    @Bean
    public DirectExchange directLogsExchange() {
        return new DirectExchange(EX_DIRECT_LOGS, false, false);
    }

    @Bean
    public Queue directLogsQueue() {
        return new AnonymousQueue();
    }

    @Bean
    public Declarables directTopology(
            DirectExchange directLogsExchange,
            Queue directLogsQueue,
            LabRabbitProperties props) {
        List<Declarable> declarables = new ArrayList<>();
        declarables.add(directLogsExchange);
        declarables.add(directLogsQueue);
        for (String severity : props.getDirectSeverities()) {
            declarables.add(
                    BindingBuilder.bind(directLogsQueue).to(directLogsExchange).with(severity.trim()));
        }
        return new Declarables(declarables);
    }

    @Bean
    public FanoutExchange notificationsExchange() {
        return new FanoutExchange(EX_NOTIFICATIONS, false, false);
    }

    @Bean
    public Queue fanoutQueueAlpha() {
        return new AnonymousQueue();
    }

    @Bean
    public Queue fanoutQueueBeta() {
        return new AnonymousQueue();
    }

    @Bean
    public Declarables fanoutTopology(
            FanoutExchange notificationsExchange,
            Queue fanoutQueueAlpha,
            Queue fanoutQueueBeta) {
        return new Declarables(
                fanoutQueueAlpha,
                fanoutQueueBeta,
                BindingBuilder.bind(fanoutQueueAlpha).to(notificationsExchange),
                BindingBuilder.bind(fanoutQueueBeta).to(notificationsExchange));
    }

    @Bean
    public TopicExchange topicOrdersExchange() {
        return new TopicExchange(EX_TOPIC_ORDERS, false, false);
    }

    @Bean
    public Queue topicOrdersQueue() {
        return new AnonymousQueue();
    }

    @Bean
    public Declarables topicTopology(
            TopicExchange topicOrdersExchange,
            Queue topicOrdersQueue,
            LabRabbitProperties props) {
        return new Declarables(
                topicOrdersExchange,
                topicOrdersQueue,
                BindingBuilder.bind(topicOrdersQueue).to(topicOrdersExchange).with(props.getTopicPattern()));
    }

    @Bean
    public Queue taskQueue() {
        return QueueBuilder.durable(Q_TASK_QUEUE).build();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

}
