package vn.edu.ou.rabbitmq.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "lab.rabbit")
public class LabRabbitProperties {
    private List<String> directSeverities = new ArrayList<>(List.of("error", "warning")); 
    private String topicPattern = "order.*"; 
    public List<String> getDirectSeverities() { 
        return directSeverities; 
    } 
    public void setDirectSeverities(List<String> directSeverities) { 
        this.directSeverities = directSeverities; 
    } 
    public String getTopicPattern() { 
        return topicPattern; 
    } 
    public void setTopicPattern(String topicPattern) { 
        this.topicPattern = topicPattern; 
    } 
}
