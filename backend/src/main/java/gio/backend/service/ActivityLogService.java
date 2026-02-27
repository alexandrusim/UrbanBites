package gio.backend.service;

import gio.backend.entity.ActivityLog;
import gio.backend.repository.ActivityLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    private final SecurityService securityService;

    @Async
    @Transactional
    public void logActivity(HttpServletRequest request, String action, Integer statusCode, Long responseTime) {
        try {
            ActivityLog log = new ActivityLog();
            
            try {
                var user = securityService.getCurrentUser();
                log.setUserId(user.getUserId());
            } catch (Exception e) {
                log.setUserId(null);
            }
            
            log.setAction(action);
            log.setEndpoint(request.getRequestURI());
            log.setHttpMethod(request.getMethod());
            log.setIpAddress(getClientIP(request));
            log.setUserAgent(request.getHeader("User-Agent"));
            log.setStatusCode(statusCode);
            log.setResponseTimeMs(responseTime);
            log.setTimestamp(LocalDateTime.now());
            
            activityLogRepository.save(log);
        } catch (Exception e) {
            System.err.println("Failed to log activity: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<ActivityLog> getAllLogs() {
        return activityLogRepository.findTop100ByOrderByTimestampDesc();
    }

    @Transactional(readOnly = true)
    public List<ActivityLog> getLogsByUserId(Integer userId) {
        return activityLogRepository.findByUserIdOrderByTimestampDesc(userId);
    }

    @Transactional(readOnly = true)
    public List<ActivityLog> getLogsByDateRange(LocalDateTime start, LocalDateTime end) {
        return activityLogRepository.findByTimestampBetweenOrderByTimestampDesc(start, end);
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
