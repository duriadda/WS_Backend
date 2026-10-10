package com.example.assignment.middleware;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 점검 모드 미들웨어.
 * app.maintenance=true 이면 컨트롤러까지 가지 않고 바로 503 을 표준 포맷으로 응답한다.
 */
@Slf4j
@Component
public class MaintenanceFilter extends OncePerRequestFilter {

    private final boolean maintenance;

    public MaintenanceFilter(@Value("${app.maintenance:false}") boolean maintenance) {
        this.maintenance = maintenance;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/api");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (!maintenance) {
            filterChain.doFilter(request, response);
            return;
        }
        // 필터는 컨트롤러 바깥이라 GlobalExceptionHandler 를 거치지 않으므로 JSON 을 직접 작성한다
        log.warn("[MAINTENANCE] {} {} -> 503", request.getMethod(), request.getRequestURI());
        response.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
        response.setHeader("Retry-After", "60");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"status\":\"error\",\"message\":\"서버 점검 중입니다. 잠시 후 다시 시도해주세요.\"}");
    }
}
