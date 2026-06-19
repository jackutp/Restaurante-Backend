package com.microservicio.solicitudes.dto.jira;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class JiraIssueDTO {
    private String key;
    private String summary;
    private String description;
    private String status;

    @JsonProperty("project_key")
    private String projectKey;
}