package com.microservicio.cambios.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservicio.cambios.dto.CambioJiraDTO;
import com.microservicio.cambios.dto.SubtareaDTO;
import com.microservicio.cambios.enums.TipoCambio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
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

        String auth = username + ":" + token;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

        this.webClient = WebClient.builder()
                .baseUrl(jiraUrl)
                .defaultHeader("Authorization", "Basic " + encodedAuth)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public String crearTicketEnJira(CambioJiraDTO cambioJira) {
        log.info("Creando ticket en Jira - Tipo: {}, Título: {}",
                cambioJira.tipoCambio(), cambioJira.titulo());

        Map<String, Object> issueData = new HashMap<>();
        Map<String, Object> fields = new HashMap<>();
        Map<String, Object> project = new HashMap<>();
        Map<String, Object> issueType = new HashMap<>();

        project.put("key", projectKey);
        issueType.put("name", "Task");

        String tipoPrefix = getTipoPrefix(cambioJira.tipoCambio());

        fields.put("project", project);
        fields.put("summary", tipoPrefix + "[" + cambioJira.riesgo() + "] " + cambioJira.titulo());
        fields.put("description", construirDescripcionConTipo(cambioJira));
        fields.put("issuetype", issueType);

        // Fecha de vencimiento
        if (cambioJira.fechaVencimiento() != null) {
            fields.put("duedate", cambioJira.fechaVencimiento().toString());
        }

        Map<String, Object> priority = new HashMap<>();
        switch (cambioJira.riesgo()) {
            case ALTO -> priority.put("name", "Highest");
            case MEDIO -> priority.put("name", "Medium");
            case BAJO -> priority.put("name", "Lowest");
        }
        if (!priority.isEmpty()) {
            fields.put("priority", priority);
        }
        // Labels
        List<String> allLabels = new ArrayList<>();
        if (cambioJira.labels() != null) {
            allLabels.addAll(cambioJira.labels());
        }
        allLabels.add(cambioJira.tipoCambio().name().toLowerCase());
        allLabels.add(cambioJira.categoriaCambio().name().toLowerCase());
        fields.put("labels", allLabels);

        // Asignado a
        if (cambioJira.assignee() != null && !cambioJira.assignee().isEmpty()) {
            Map<String, Object> assignee = new HashMap<>();
            assignee.put("name", cambioJira.assignee());
            fields.put("assignee", assignee);
        }

        issueData.put("fields", fields);

        try {
            String response = webClient.post()
                    .uri("/rest/api/2/issue")
                    .bodyValue(issueData)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode jsonNode = objectMapper.readTree(response);
            String issueKey = jsonNode.get("key").asText();

            log.info("Ticket Jira creado: {}", issueKey);

            // Crear subtareas
            if (cambioJira.subtareas() != null && !cambioJira.subtareas().isEmpty()) {
                crearSubtareas(issueKey, cambioJira.subtareas());
            }

            return issueKey;

        } catch (Exception e) {
            log.error("Error al crear ticket en Jira: {}", e.getMessage());
            throw new RuntimeException("Error al crear ticket en Jira", e);
        }
    }

    private String getTipoPrefix(TipoCambio tipo) {
        return switch (tipo) {
            case NORMAL -> "[NORMAL]";
            case EMERGENCIA -> "[EMERGENCIA]";
            case REPETITIVO -> "[REPETITIVO]";
        };
    }

    private String construirDescripcionConTipo(CambioJiraDTO dto) {
        StringBuilder desc = new StringBuilder();

        desc.append("h2. INFORMACIÓN DEL CAMBIO\n\n");
        desc.append("*Tipo:* ").append(dto.tipoCambio()).append("\n");
        desc.append("*Categoria:* ").append(dto.categoriaCambio()).append("\n");
        desc.append("*Sistema afectado:* ").append(dto.sistemaAfectado()).append("\n");
        desc.append("*Riesgo:* ").append(dto.riesgo()).append("\n");
        desc.append("h2. DESCRIPCIÓN\n\n");
        desc.append(dto.descripcion()).append("\n\n");
        if (dto.fechaImplementacion() != null) {
            desc.append("h2. FECHA DE IMPLEMENTACIÓN\n\n");
            desc.append(dto.fechaImplementacion()).append("\n\n");
        }
        if (dto.planRollback() != null && !dto.planRollback().isBlank()) {
            desc.append("h2. PLAN DE ROLLBACK\n\n");
            desc.append(dto.planRollback()).append("\n\n");
        }
        return desc.toString();
    }

    private void crearSubtareas(String parentKey, List<SubtareaDTO> subtareas) {
        for (SubtareaDTO subtarea : subtareas) {
            Map<String, Object> subtaskData = new HashMap<>();
            Map<String, Object> fields = new HashMap<>();
            Map<String, Object> project = new HashMap<>();
            Map<String, Object> issueType = new HashMap<>();
            Map<String, Object> parent = new HashMap<>();

            project.put("key", projectKey);
            issueType.put("name", "Subtask");
            parent.put("key", parentKey);

            fields.put("project", project);
            fields.put("summary", subtarea.titulo());
            fields.put("description", subtarea.descripcion());
            fields.put("issuetype", issueType);
            fields.put("parent", parent);

            subtaskData.put("fields", fields);

            try {
                webClient.post()
                        .uri("/rest/api/2/issue")
                        .bodyValue(subtaskData)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
                log.info("Subtarea creada: {}", subtarea.titulo());
            } catch (Exception e) {
                log.error("Error al crear subtarea {}: {}", subtarea.titulo(), e.getMessage());
            }
        }
    }

    public void actualizarEstadoJira(String issueKey, String estado) {
        log.info("Actualizando ticket {} a estado: {}", issueKey, estado);

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
        }
    }

    public void actualizarAsignado(String issueKey, String accountId) {
        Map<String, Object> body = new HashMap<>();
        body.put("accountId", accountId);
        try {
            webClient.put()
                    .uri("/rest/api/3/issue/{issueKey}/assignee", issueKey)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
            log.info("Asignado actualizado en Jira para {}", issueKey);
        } catch (Exception e) {
            log.error("Error asignando ticket Jira: {}", e.getMessage());
        }
    }

    private String mapEstadoToTransitionId(String estado) {
        return switch (estado) {
            case "PENDIENTE" -> "11";
            case "EN_REVISION" -> "21";
            case "APROBADO" -> "31";
            case "RECHAZADO" -> "41";
            case "EN_IMPLEMENTACION" -> "51";
            case "IMPLEMENTADO" -> "2";
            case "ROLLBACK" -> "3";
            case "CERRADO" -> "4";
            default -> throw new IllegalArgumentException("Estado no soportado: " + estado); //Pendiente, el default duh
        };
    }

    public String getTicketUrl(String issueKey) {
        return jiraUrl + "/browse/" + issueKey;
    }
}