package vn.edu.ou.rabbitmq.dto;

import java.time.Instant; 

public record NotificationPayload(String type, String message, Instant 
timestamp) { 
}
