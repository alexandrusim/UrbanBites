package gio.backend.service;

import gio.backend.entity.ContactMessage;
import gio.backend.repository.ContactMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContactMessageService {

    private final ContactMessageRepository contactMessageRepository;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public List<ContactMessage> getAllMessages() {
        return contactMessageRepository.findAll();
    }

    @Transactional(readOnly = true)
    public ContactMessage getMessageById(Integer id) {
        return contactMessageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contact message not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<ContactMessage> getMessagesByRestaurant(Integer restaurantId) {
        return contactMessageRepository.findByRestaurantId(restaurantId);
    }

    @Transactional(readOnly = true)
    public List<ContactMessage> getPendingMessages() {
        if (securityService.isSystemAdmin()) {
            return contactMessageRepository.findAll().stream()
                    .filter(m -> "NEW".equals(m.getStatus()) || "PENDING".equals(m.getStatus()))
                    .collect(Collectors.toList());
        }
        throw new RuntimeException("Access denied");
    }

    @Transactional
    public ContactMessage createMessage(ContactMessage message) {
        message.setStatus("NEW");
        message.setCreatedAt(LocalDateTime.now());
        return contactMessageRepository.save(message);
    }

    @Transactional
    public ContactMessage updateMessageStatus(Integer id, String status, String adminReply) {
        ContactMessage message = getMessageById(id);
        message.setStatus(status);
        if (adminReply != null && !adminReply.isEmpty()) {
            message.setAdminReply(adminReply);
            message.setRepliedAt(LocalDateTime.now());
        }
        return contactMessageRepository.save(message);
    }

    @Transactional
    public void deleteMessage(Integer id) {
        if (!contactMessageRepository.existsById(id)) {
            throw new RuntimeException("Contact message not found with id: " + id);
        }
        contactMessageRepository.deleteById(id);
    }
}
