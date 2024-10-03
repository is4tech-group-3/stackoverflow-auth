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

    @Value("${rest.audit.api.url}")
    private String apiUrl;

    private final WebClient webClient = WebClient.create();

    public void auditPost(Audit audit) {
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
