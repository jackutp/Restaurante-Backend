package com.microservicio.solicitudes.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class JiraService {

    private final WebClient webClient;
    private final String jiraUrl;
    private final String projectKey;
    private final ObjectMapper objectMapper;

    public JiraService(
            @Value("${jira.url}") String jiraUrl,
            @Value("${jira.username}") String username,
            @Value("${jira.token}") String token,
            @Value("${jira.project.key}") String projectKey) {

        this.jiraUrl = jiraUrl;
        this.projectKey = projectKey;
        this.objectMapper = new ObjectMapper();

        // Codificar credenciales
        String auth = username + ":" + token;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

        this.webClient = WebClient.builder()
                .baseUrl(jiraUrl)
                .defaultHeader("Authorization", "Basic " + encodedAuth)
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json")
                .build();
    }

    public String crearTicketEnJira(String summary, String description) {
        log.info("Creando ticket en Jira - Summary: {}", summary);

        Map<String, Object> issueData = new HashMap<>();
        Map<String, Object> fields = new HashMap<>();
        Map<String, Object> project = new HashMap<>();
        Map<String, Object> issueType = new HashMap<>();

        project.put("key", projectKey);
        issueType.put("name", "Task");

        fields.put("project", project);
        fields.put("summary", summary);
        fields.put("description", description);
        fields.put("issuetype", issueType);

        issueData.put("fields", fields);

        try {
            String response = webClient.post()
                    .uri("/rest/api/2/issue")
                    .bodyValue(issueData)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // Extraer el key del ticket de la respuesta
            JsonNode jsonNode = objectMapper.readTree(response);
            String issueKey = jsonNode.get("key").asText();

            log.info("Ticket creado exitosamente: {}", issueKey);
            return issueKey;

        } catch (Exception e) {
            log.error("Error al crear ticket en Jira: {}", e.getMessage());
            throw new RuntimeException("Error al crear ticket en Jira: " + e.getMessage(), e);
        }
    }

    public void actualizarEstadoJira(String issueKey, String estado) {
        log.info("Actualizando ticket {} a estado: {}", issueKey, estado);

        // Mapeo de estados locales a transiciones de Jira
        String transitionId = mapEstadoToTransitionId(estado);

        Map<String, Object> transitionData = new HashMap<>();
        Map<String, Object> transition = new HashMap<>();
        transition.put("id", transitionId);
        transitionData.put("transition", transition);

        try {
            webClient.post()
                    .uri("/rest/api/2/issue/{issueKey}/transitions", issueKey)
                    .bodyValue(transitionData)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();

            log.info("Ticket {} actualizado exitosamente", issueKey);

        } catch (Exception e) {
            log.error("Error al actualizar ticket Jira: {}", e.getMessage());
            throw new RuntimeException("Error al actualizar ticket Jira: " + e.getMessage(), e);
        }
    }

    private String mapEstadoToTransitionId(String estado) {
        // Estos IDs dependen del workflow de tu proyecto en Jira
        // Puedes obtenerlos con GET /rest/api/2/issue/{issueKey}/transitions
        return switch (estado) {
            case "EN_PROCESO" -> "3";   // ID para "In Progress"
            case "COMPLETADA" -> "4";    // ID para "Done"
            case "RECHAZADA" -> "5";     // ID para "Rejected"
            default -> "1";               // ID por defecto
        };
    }

    public String getJiraUrl() {
        return jiraUrl;
    }

    public String getTicketUrl(String issueKey) {
        return jiraUrl + "/browse/" + issueKey;
    }
}