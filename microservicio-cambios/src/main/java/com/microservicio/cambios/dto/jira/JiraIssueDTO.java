package com.microservicio.cambios.dto.jira;

import com.fasterxml.jackson.annotation.JsonProperty;

public record JiraIssueDTO(
        String key,
        String summary,
        String description,
        String status,

        @JsonProperty("project_key")
        String projectKey
) {

}