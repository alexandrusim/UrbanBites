package gio.backend.service;

import gio.backend.dto.NotificationDTO;
import gio.backend.entity.Notification;
import gio.backend.repository.NotificationRepository;
import gio.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public List<NotificationDTO> getAllNotifications() {
        return notificationRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public NotificationDTO getNotificationById(Integer id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notificarea cu ID " + id + " nu a fost găsită"));
        return convertToDTO(notification);
    }

    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsByUserId(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Utilizatorul cu ID " + userId + " nu există");
        }
        
        securityService.checkUserAccess(userId);
        
        return notificationRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NotificationDTO> getUnreadNotificationsByUserId(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Utilizatorul cu ID " + userId + " nu există");
        }
        
        securityService.checkUserAccess(userId);
        
        return notificationRepository.findByUserIdAndStatus(userId, "PENDING").stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public NotificationDTO createNotification(NotificationDTO notificationDTO) {
        if (!userRepository.existsById(notificationDTO.getUserId())) {
            throw new RuntimeException("Utilizatorul cu ID " + notificationDTO.getUserId() + " nu există");
        }

        Notification notification = convertToEntity(notificationDTO);
        Notification savedNotification = notificationRepository.save(notification);
        return convertToDTO(savedNotification);
    }

    @Transactional
    public NotificationDTO markAsRead(Integer id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notificarea cu ID " + id + " nu a fost găsită"));

        securityService.checkUserAccess(notification.getUserId());
        
        notification.setStatus("READ");
        notification.setReadAt(LocalDateTime.now());

        Notification updatedNotification = notificationRepository.save(notification);
        return convertToDTO(updatedNotification);
    }

    @Transactional
    public void markAllAsReadForUser(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Utilizatorul cu ID " + userId + " nu există");
        }
        
        securityService.checkUserAccess(userId);

        List<Notification> unreadNotifications = notificationRepository.findByUserIdAndStatus(userId, "PENDING");
        LocalDateTime now = LocalDateTime.now();

        unreadNotifications.forEach(notification -> {
            notification.setStatus("READ");
            notification.setReadAt(now);
        });

        notificationRepository.saveAll(unreadNotifications);
    }

    @Transactional
    public void deleteNotification(Integer id) {
        if (!notificationRepository.existsById(id)) {
            throw new RuntimeException("Notificarea cu ID " + id + " nu a fost găsită");
        }
        notificationRepository.deleteById(id);
    }

    @Transactional
    public void deleteAllForUser(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Utilizatorul cu ID " + userId + " nu există");
        }
        notificationRepository.deleteByUserId(userId);
    }

    private NotificationDTO convertToDTO(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setNotificationId(notification.getNotificationId());
        dto.setUserId(notification.getUserId());
        dto.setType(notification.getType());
        dto.setTitle(notification.getTitle());
        dto.setMessage(notification.getMessage());
        dto.setRelatedType(notification.getRelatedType());
        dto.setRelatedId(notification.getRelatedId());
        dto.setRelatedReservationId(notification.getRelatedId()); 
        dto.setStatus(notification.getStatus());
        dto.setIsRead("READ".equals(notification.getStatus()));
        dto.setCreatedAt(notification.getCreatedAt());
        dto.setSentAt(notification.getSentAt());
        dto.setReadAt(notification.getReadAt());
        return dto;
    }

    private Notification convertToEntity(NotificationDTO dto) {
        Notification notification = new Notification();
        notification.setUserId(dto.getUserId());
        notification.setType(dto.getType());
        notification.setTitle(dto.getTitle());
        notification.setMessage(dto.getMessage());
        notification.setRelatedType(dto.getRelatedType() != null ? dto.getRelatedType() : "RESERVATION");
        notification.setRelatedId(dto.getRelatedId() != null ? dto.getRelatedId() : dto.getRelatedReservationId());
        notification.setStatus(dto.getStatus() != null ? dto.getStatus() : "PENDING");
        notification.setRetryCount(0);
        return notification;
    }
}
