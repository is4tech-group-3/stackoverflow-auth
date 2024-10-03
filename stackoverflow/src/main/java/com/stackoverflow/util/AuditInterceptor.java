package com.stackoverflow.util;

import com.stackoverflow.bo.Audit;
import com.stackoverflow.bo.User;
import com.stackoverflow.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;


import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.Date;

@AllArgsConstructor
@Component
public class AuditInterceptor implements HandlerInterceptor {
    private final AuditRequest auditRequest;
    private final CurrentUser currentUser;
    private final UserRepository userRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            Method method = ((HandlerMethod) handler).getMethod();
            AuditAnnotation auditAnnotation = method.getAnnotation(AuditAnnotation.class);

            if (!(request instanceof ContentCachingRequestWrapper)) {
                request = new ContentCachingRequestWrapper(request);
            }

            if (auditAnnotation != null) {
                Audit audit = new Audit();

                audit.setEntity(auditAnnotation.value());
                audit.setHttpMethod(request.getMethod());
                audit.setDateOperation(new Date());
                User user = getUserIdFromRequest(request);
                audit.setUserId(user.getId());
                audit.setEmail(user.getEmail());

                request.setAttribute("audit", audit);
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (handler instanceof HandlerMethod) {
            Audit audit = (Audit) request.getAttribute("audit");

            if (audit != null) {
                ContentCachingRequestWrapper cachingRequest = (ContentCachingRequestWrapper) request;
                String requestBody = getRequestBody(cachingRequest);
                if (!requestBody.isEmpty() && request.getContentLength() > 0) {
                    audit.setRequest(requestBody);
                } else {
                    audit.setRequest("NO REQUEST BODY");
                }
                audit.setStatusCode(response.getStatus());
                audit.setStatusDescription(HttpStatus.valueOf(response.getStatus()).getReasonPhrase());

                Object responseBody = request.getAttribute("responseBody");
                if (responseBody != null) {
                    audit.setResponse(responseBody);
                } else {
                    audit.setResponse("NO RESPONSE BODY");
                }

                auditRequest.auditPost(audit);
            }
        }
    }

    private String getRequestBody(ContentCachingRequestWrapper request) {
        byte[] content = request.getContentAsByteArray();
        try {
            return new String(content, request.getCharacterEncoding());
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException("Encoding not supported request body");
        }
    }

    private User getUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            String email = currentUser.extractUserId(token);
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
        }
        return null;
    }
}
