package gio.backend.service;

import gio.backend.dto.NotificationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AsyncNotificationService {

    private final NotificationService notificationService;

    @Async
    public void sendReservationConfirmation(Integer userId, Integer reservationId, String restaurantName, String reservationDate) {
        try {
            NotificationDTO notification = new NotificationDTO();
            notification.setUserId(userId);
            notification.setType("RESERVATION_CONFIRMED");
            notification.setTitle("Rezervare confirmată");
            notification.setMessage(String.format("Rezervarea dvs. la %s pentru data %s a fost confirmată.", 
                restaurantName, reservationDate));
            notification.setRelatedType("RESERVATION");
            notification.setRelatedId(reservationId);
            notification.setStatus("PENDING");

            notificationService.createNotification(notification);
            log.info("Notificare de confirmare trimisă pentru utilizatorul {} și rezervarea {}", userId, reservationId);
        } catch (Exception e) {
            log.error("Eroare la trimiterea notificării de confirmare: {}", e.getMessage());
        }
    }

    @Async
    public void sendReservationCancellation(Integer userId, Integer reservationId, String restaurantName) {
        try {
            NotificationDTO notification = new NotificationDTO();
            notification.setUserId(userId);
            notification.setType("RESERVATION_CANCELLED");
            notification.setTitle("Rezervare anulată");
            notification.setMessage(String.format("Rezervarea dvs. la %s a fost anulată.", restaurantName));
            notification.setRelatedType("RESERVATION");
            notification.setRelatedId(reservationId);
            notification.setStatus("PENDING");

            notificationService.createNotification(notification);
            log.info("Notificare de anulare trimisă pentru utilizatorul {} și rezervarea {}", userId, reservationId);
        } catch (Exception e) {
            log.error("Eroare la trimiterea notificării de anulare: {}", e.getMessage());
        }
    }

    @Async
    public void sendReservationPending(Integer userId, Integer reservationId, String restaurantName, String reservationDate) {
        try {
            NotificationDTO notification = new NotificationDTO();
            notification.setUserId(userId);
            notification.setType("RESERVATION_PENDING");
            notification.setTitle("Rezervare în așteptare");
            notification.setMessage(String.format("Rezervarea dvs. la %s pentru data %s este în așteptarea aprobării.", 
                restaurantName, reservationDate));
            notification.setRelatedType("RESERVATION");
            notification.setRelatedId(reservationId);
            notification.setStatus("PENDING");

            notificationService.createNotification(notification);
            log.info("Notificare de rezervare în așteptare trimisă pentru utilizatorul {} și rezervarea {}", userId, reservationId);
        } catch (Exception e) {
            log.error("Eroare la trimiterea notificării de rezervare în așteptare: {}", e.getMessage());
        }
    }

    @Async
    public void sendReservationStatusUpdate(Integer userId, Integer reservationId, String restaurantName, String newStatus) {
        try {
            String title = "";
            String message = "";
            String type = "";

            switch (newStatus.toUpperCase()) {
                case "CONFIRMED":
                    title = "Rezervare confirmată";
                    message = String.format("Rezervarea dvs. la %s a fost confirmată de restaurant.", restaurantName);
                    type = "RESERVATION_CONFIRMED";
                    break;
                case "CANCELLED":
                    title = "Rezervare anulată";
                    message = String.format("Rezervarea dvs. la %s a fost anulată.", restaurantName);
                    type = "RESERVATION_CANCELLED";
                    break;
                case "COMPLETED":
                    title = "Rezervare finalizată";
                    message = String.format("Vă mulțumim că ați ales %s! Sperăm că v-a plăcut experiența.", restaurantName);
                    type = "RESERVATION_COMPLETED";
                    break;
                case "NO_SHOW":
                    title = "Rezervare - absență";
                    message = String.format("Ne pare rău că nu ați putut ajunge la rezervarea de la %s.", restaurantName);
                    type = "RESERVATION_NO_SHOW";
                    break;
                default:
                    title = "Actualizare rezervare";
                    message = String.format("Statusul rezervării dvs. la %s a fost actualizat: %s", restaurantName, newStatus);
                    type = "RESERVATION_UPDATE";
            }

            NotificationDTO notification = new NotificationDTO();
            notification.setUserId(userId);
            notification.setType(type);
            notification.setTitle(title);
            notification.setMessage(message);
            notification.setRelatedType("RESERVATION");
            notification.setRelatedId(reservationId);
            notification.setStatus("PENDING");

            notificationService.createNotification(notification);
            log.info("Notificare de actualizare status trimisă pentru utilizatorul {} și rezervarea {}", userId, reservationId);
        } catch (Exception e) {
            log.error("Eroare la trimiterea notificării de actualizare status: {}", e.getMessage());
        }
    }

    @Async
    public void sendGuestReservationCreated(String guestEmail, String guestName, Integer reservationId, String restaurantName, String reservationDate) {
        try {
            log.info("Notificare pentru guest: {} ({}) - Rezervare {} la {} pentru {}", 
                guestName, guestEmail, reservationId, restaurantName, reservationDate);
        } catch (Exception e) {
            log.error("Eroare la trimiterea notificării pentru guest: {}", e.getMessage());
        }
    }

    @Async
    public void sendPaymentConfirmation(Integer userId, Integer paymentId, String amount, String restaurantName) {
        try {
            NotificationDTO notification = new NotificationDTO();
            notification.setUserId(userId);
            notification.setType("PAYMENT_CONFIRMED");
            notification.setTitle("Plată confirmată");
            notification.setMessage(String.format("Plata de %s RON pentru rezervarea la %s a fost procesată cu succes.", 
                amount, restaurantName));
            notification.setRelatedType("PAYMENT");
            notification.setRelatedId(paymentId);
            notification.setStatus("PENDING");

            notificationService.createNotification(notification);
            log.info("Notificare de plată confirmată trimisă pentru utilizatorul {} și plata {}", userId, paymentId);
        } catch (Exception e) {
            log.error("Eroare la trimiterea notificării de plată: {}", e.getMessage());
        }
    }
}
