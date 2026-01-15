package ru.ifmo.calculatingservice.config;

import com.netflix.concurrency.limits.Limiter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LimiterInterceptor implements HandlerInterceptor {
    private Logger log = LoggerFactory.getLogger(LimiterInterceptor.class);

    @Autowired
    private Limiter<String> concurrencyLimiter;

    private ThreadLocal<Limiter.Listener> listenerThreadLocal = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String path = request.getRequestURI();

        if (path.startsWith("/api/route/")) {
            log.debug("Interceptor checking limiter for: {}", path);

            Optional<Limiter.Listener> listener = concurrencyLimiter.acquire(path);

            if (!listener.isPresent()) {
                log.warn("Interceptor REJECTED request to: {}", path);
                response.setStatus(429);
                response.setContentType("application/json");
                try {
                    response.getWriter().write("{\"error\":\"Service overloaded\",\"status\":429}");
                } catch (Exception e) {
                    log.error("Error writing response", e);
                }
                return false;
            }

            listenerThreadLocal.set(listener.get());
            log.debug("✅ Interceptor ALLOWED request to: {}", path);
        }

        return true;
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        Limiter.Listener listener = listenerThreadLocal.get();
        if (listener != null) {
            if (ex == null && response.getStatus() < 400) {
                listener.onSuccess();
                log.debug("Request completed successfully");
            } else {
                listener.onIgnore();
                log.debug("Request failed or errored");
            }
            listenerThreadLocal.remove();
        }
    }
}
