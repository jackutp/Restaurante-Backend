package com.microservicio.cambios.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservicio.cambios.dto.solicitud_servicio.SolicitudJiraDTO;
import com.microservicio.cambios.dto.solicitud_servicio.SubtareaDTO;
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

    public String crearTicketEnJira(SolicitudJiraDTO solicitudJira) {
        log.info("Creando ticket en Jira - Tipo: {}, Título: {}",
                solicitudJira.getTipoSolicitud(), solicitudJira.getTitulo());

        Map<String, Object> issueData = new HashMap<>();
        Map<String, Object> fields = new HashMap<>();
        Map<String, Object> project = new HashMap<>();
        Map<String, Object> issueType = new HashMap<>();

        project.put("key", projectKey);
        issueType.put("name", "Task");

        String tipoPrefix = getTipoPrefix(solicitudJira.getTipoSolicitud());

        fields.put("project", project);
        fields.put("summary", tipoPrefix + " " + solicitudJira.getTitulo());
        fields.put("description", construirDescripcionConTipo(solicitudJira));
        fields.put("issuetype", issueType);

        // Prioridad
        if (solicitudJira.getPrioridad() != null) {
            Map<String, Object> priority = new HashMap<>();
            priority.put("name", solicitudJira.getPrioridad());
            fields.put("priority", priority);
        }

        // Fecha de vencimiento
        if (solicitudJira.getFechaVencimiento() != null) {
            fields.put("duedate", solicitudJira.getFechaVencimiento().toString());
        }

        // Labels
        List<String> allLabels = new ArrayList<>();
        if (solicitudJira.getLabels() != null) {
            allLabels.addAll(solicitudJira.getLabels());
        }
        allLabels.add(solicitudJira.getTipoSolicitud().toString().toLowerCase());
        fields.put("labels", allLabels);

        // Asignado a
        if (solicitudJira.getAssignee() != null && !solicitudJira.getAssignee().isEmpty()) {
            Map<String, Object> assignee = new HashMap<>();
            assignee.put("name", solicitudJira.getAssignee());
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
            if (solicitudJira.getSubtareas() != null && !solicitudJira.getSubtareas().isEmpty()) {
                crearSubtareas(issueKey, solicitudJira.getSubtareas());
            }

            return issueKey;

        } catch (Exception e) {
            log.error("Error al crear ticket en Jira: {}", e.getMessage());
            throw new RuntimeException("Error al crear ticket en Jira", e);
        }
    }

    private String getTipoPrefix(TipoSolicitud tipo) {
        return switch (tipo) {
            case SERVICIO -> "[SERVICIO]";
            case INFORMACION -> "[INFO]";
            case ACCESO -> "[ACCESO]";
        };
    }

    private String construirDescripcionConTipo(SolicitudJiraDTO dto) {
        StringBuilder desc = new StringBuilder();

        desc.append("h2. TIPO DE SOLICITUD\n\n");
        desc.append("*").append(dto.getTipoSolicitud().getNombre()).append("*\n\n");

        switch (dto.getTipoSolicitud()) {
            case SERVICIO -> desc.append("_Soporte técnico, atención, configuración_\n\n");
            case INFORMACION -> desc.append("_Datos, reportes, estado de servicios_\n\n");
            case ACCESO -> desc.append("_Permisos, usuarios, roles_\n\n");
        }

        desc.append("h2. DESCRIPCIÓN\n\n");
        desc.append(dto.getDescripcion()).append("\n\n");

        if (dto.getFechaVencimiento() != null) {
            desc.append("h2. FECHA DE VENCIMIENTO\n\n");
            desc.append(dto.getFechaVencimiento()).append("\n\n");
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
            fields.put("summary", subtarea.getTitulo());
            fields.put("description", subtarea.getDescripcion());
            fields.put("issuetype", issueType);
            fields.put("parent", parent);

            if (subtarea.getPrioridad() != null) {
                Map<String, Object> priority = new HashMap<>();
                priority.put("name", subtarea.getPrioridad());
                fields.put("priority", priority);
            }

            subtaskData.put("fields", fields);

            try {
                webClient.post()
                        .uri("/rest/api/2/issue")
                        .bodyValue(subtaskData)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
                log.info("Subtarea creada: {}", subtarea.getTitulo());
            } catch (Exception e) {
                log.error("Error al crear subtarea {}: {}", subtarea.getTitulo(), e.getMessage());
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

    private String mapEstadoToTransitionId(String estado) {
        return switch (estado) {
            case "EN_PROCESO" -> "3";
            case "COMPLETADA" -> "4";
            case "RECHAZADA" -> "5";
            default -> "1";
        };
    }

    public String getTicketUrl(String issueKey) {
        return jiraUrl + "/browse/" + issueKey;
    }
}