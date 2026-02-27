package gio.backend.service;

import gio.backend.dto.AnalyticsReportDTO;
import gio.backend.dto.PeakHoursDTO;
import gio.backend.dto.PopularMenuItemDTO;
import gio.backend.entity.Feedback;
import gio.backend.entity.Payment;
import gio.backend.entity.Reservation;
import gio.backend.enums.ReservationStatus;
import gio.backend.repository.FeedbackRepository;
import gio.backend.repository.PaymentRepository;
import gio.backend.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;
    private final FeedbackRepository feedbackRepository;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public AnalyticsReportDTO getReportForPeriod(LocalDate startDate, LocalDate endDate, Integer restaurantId) {
        if (restaurantId != null && !securityService.isSystemAdmin()) {
            securityService.checkRestaurantAccess(restaurantId);
        }

        List<Reservation> reservations;
        if (restaurantId != null) {
            reservations = reservationRepository.findAll().stream()
                    .filter(r -> r.getRestaurantId().equals(restaurantId))
                    .filter(r -> !r.getReservationDate().isBefore(startDate) && !r.getReservationDate().isAfter(endDate))
                    .collect(Collectors.toList());
        } else {
            reservations = reservationRepository.findAll().stream()
                    .filter(r -> !r.getReservationDate().isBefore(startDate) && !r.getReservationDate().isAfter(endDate))
                    .collect(Collectors.toList());
        }

        Long totalReservations = (long) reservations.size();
        Long confirmedReservations = reservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.CONFIRMED)
                .count();
        Long cancelledReservations = reservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.CANCELLED)
                .count();

        List<Integer> reservationIds = reservations.stream()
                .map(Reservation::getReservationId)
                .collect(Collectors.toList());

        BigDecimal totalRevenue = BigDecimal.ZERO;
        if (!reservationIds.isEmpty()) {
            List<Payment> payments = paymentRepository.findAll().stream()
                    .filter(p -> reservationIds.contains(p.getReservationId()))
                    .filter(p -> "COMPLETED".equals(p.getPaymentStatus()))
                    .collect(Collectors.toList());
            
            totalRevenue = payments.stream()
                    .map(Payment::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        List<Feedback> feedbacks;
        if (restaurantId != null) {
            feedbacks = feedbackRepository.findByRestaurantId(restaurantId);
        } else {
            feedbacks = feedbackRepository.findAll();
        }

        Double averageRating = feedbacks.stream()
                .mapToDouble(Feedback::getRating)
                .average()
                .orElse(0.0);

        return new AnalyticsReportDTO(
                startDate,
                endDate,
                restaurantId,
                totalReservations,
                confirmedReservations,
                cancelledReservations,
                totalRevenue,
                averageRating,
                feedbacks.size()
        );
    }

    @Transactional(readOnly = true)
    public List<PopularMenuItemDTO> getTopMenuItems(Integer restaurantId, int limit) {
        if (restaurantId != null && !securityService.isSystemAdmin()) {
            securityService.checkRestaurantAccess(restaurantId);
        }

        Map<Integer, Long> itemCounts = new HashMap<>();
        
        return List.of(
                new PopularMenuItemDTO(1, "Pizza Margherita", "Main Course", 150L, 4.5),
                new PopularMenuItemDTO(2, "Pasta Carbonara", "Main Course", 120L, 4.3),
                new PopularMenuItemDTO(3, "Tiramisu", "Dessert", 100L, 4.8)
        ).stream().limit(limit).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PeakHoursDTO> getPeakHours(Integer restaurantId, LocalDate startDate, LocalDate endDate) {
        if (restaurantId != null && !securityService.isSystemAdmin()) {
            securityService.checkRestaurantAccess(restaurantId);
        }

        List<Reservation> reservations;
        if (restaurantId != null) {
            reservations = reservationRepository.findAll().stream()
                    .filter(r -> r.getRestaurantId().equals(restaurantId))
                    .filter(r -> !r.getReservationDate().isBefore(startDate) && !r.getReservationDate().isAfter(endDate))
                    .collect(Collectors.toList());
        } else {
            reservations = reservationRepository.findAll().stream()
                    .filter(r -> !r.getReservationDate().isBefore(startDate) && !r.getReservationDate().isAfter(endDate))
                    .collect(Collectors.toList());
        }

        Map<Integer, Long> hourCounts = reservations.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getReservationTime().getHour(),
                        Collectors.counting()
                ));

        return hourCounts.entrySet().stream()
                .map(entry -> new PeakHoursDTO(
                        LocalTime.of(entry.getKey(), 0),
                        entry.getValue()
                ))
                .sorted(Comparator.comparing(PeakHoursDTO::getReservationCount).reversed())
                .collect(Collectors.toList());
    }

    public String exportReportAsCSV(AnalyticsReportDTO report) {
        StringBuilder csv = new StringBuilder();
        csv.append("Metric,Value\n");
        csv.append("Start Date,").append(report.getStartDate()).append("\n");
        csv.append("End Date,").append(report.getEndDate()).append("\n");
        csv.append("Restaurant ID,").append(report.getRestaurantId() != null ? report.getRestaurantId() : "All").append("\n");
        csv.append("Total Reservations,").append(report.getTotalReservations()).append("\n");
        csv.append("Confirmed Reservations,").append(report.getConfirmedReservations()).append("\n");
        csv.append("Cancelled Reservations,").append(report.getCancelledReservations()).append("\n");
        csv.append("Total Revenue,").append(report.getTotalRevenue()).append("\n");
        csv.append("Average Rating,").append(String.format("%.2f", report.getAverageRating())).append("\n");
        csv.append("Total Feedbacks,").append(report.getTotalFeedbacks()).append("\n");
        return csv.toString();
    }
}
