package com.stackoverflow.util;

import com.stackoverflow.bo.Audit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Date;

@Service
public class AuditRequest {

    @Value("${rest.audit.api.url:http://localhost:3000/api/v1/audit}")
    private String apiUrl;

    private final WebClient webClient = WebClient.create();

    public void auditPost(
            String entity, String operation,
            Object request, Object response,
            Integer codeStatus, String descriptionState,
            String username, Long userId
    ) {
        Audit audit = Audit.builder()
                .entity(entity)
                .httpMethod(operation)
                .request(request)
                .response(response)
                .statusCode(codeStatus)
                .statusDescription(descriptionState)
                .dateOperation(new Date())
                .username(username)
                .userId(userId)
                .build();

        LoggerUtil.loggerDebug(apiUrl);
       webClient.post()
                .uri(apiUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(audit), Audit.class)
                .retrieve()
                .bodyToMono(Audit.class)
                .doOnSuccess(result -> LoggerUtil.loggerDebug("Audit saved successfully"))
                .doOnError(e -> LoggerUtil.loggerDebug("Error saved audit"))
                .subscribe();

    }
}
