package vn.edu.ou.rabbitmq.web;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JacksonException;

import jakarta.validation.constraints.NotBlank;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.edu.ou.rabbitmq.dto.NotificationPayload;
import vn.edu.ou.rabbitmq.dto.TaskPayload;
import vn.edu.ou.rabbitmq.publisher.LabMessagePublisher;

@RestController
@RequestMapping("/api/lab")
public class LabRestController {

    private final LabMessagePublisher publisher;

    public LabRestController(LabMessagePublisher publisher) {
        this.publisher = publisher;
    }

    @PostMapping("/direct/demo")
    public ResponseEntity<Map<String, Object>> publishDirectDemo() {
        publisher.publishAllDirectSeverities();
        return ResponseEntity.ok(Map.of("status", "sent", "severities", List.of("info", "warning", "error")));
    }

    @PostMapping("/direct")
    public ResponseEntity<Map<String, String>> publishDirect(
            @RequestParam @NotBlank String severity,
            @RequestParam(required = false) String text) {
        String message = text != null && !text.isBlank()
                ? text
                : "[" + severity.toUpperCase() + "] Message at " + Instant.now();
        publisher.publishDirect(severity, message);
        return ResponseEntity.ok(Map.of("exchange", "direct_logs", "severity", severity));
    }

    @PostMapping("/fanout")
    public ResponseEntity<Map<String, String>> publishFanout(
            @RequestBody(required = false) NotificationPayload payload) throws JacksonException {
        if (payload == null) {
            publisher.publishFanoutDefaultMaintenance();
        } else {
            publisher.publishFanout(payload);
        }
        return ResponseEntity.ok(Map.of("exchange", "notifications", "mode", "broadcast"));
    }

    @PostMapping("/topic")
    public ResponseEntity<Map<String, Object>> publishTopic(
            @RequestParam @NotBlank String routingKey,
            @RequestBody Map<String, Object> body) throws JacksonException {
        publisher.publishTopic(routingKey, body);
        return ResponseEntity.ok(Map.of("exchange", "topic_orders", "routingKey", routingKey));
    }

    @PostMapping("/tasks/demo")
    public ResponseEntity<Map<String, Object>> publishTasksDemo() throws JacksonException {
        List<TaskPayload> tasks = List.of(
                new TaskPayload(1, "email", Map.of("to", "user@example.com")),
                new TaskPayload(2, "resize", Map.of("image", "photo.jpg")),
                new TaskPayload(3, "report", Map.of("format", "pdf")));
        for (TaskPayload task : tasks) {
            publisher.sendTask(task);
        }
        return ResponseEntity.ok(Map.of("status", "sent", "count", tasks.size()));
    }

    @PostMapping("/tasks")
    public ResponseEntity<Map<String, Object>> publishTask(@RequestBody TaskPayload task)
            throws JacksonException {
        publisher.sendTask(task);
        return ResponseEntity.ok(new LinkedHashMap<>(Map.of("queue", "task_queue", "id", task.id())));
    }

}
