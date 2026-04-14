package vn.edu.ou.rabbitmq.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties; 

@JsonIgnoreProperties(ignoreUnknown = true) 
public record TaskPayload(int id, String type, Object data) { 
}