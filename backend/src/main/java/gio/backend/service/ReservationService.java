package gio.backend.service;

import gio.backend.dto.*;
import gio.backend.entity.Reservation;
import gio.backend.entity.Restaurant;
import gio.backend.entity.Table;
import gio.backend.entity.User;
import gio.backend.enums.ReservationStatus;
import gio.backend.repository.ReservationRepository;
import gio.backend.repository.RestaurantRepository;
import gio.backend.repository.TableRepository;
import gio.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private TableRepository tableRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AsyncNotificationService asyncNotificationService;
    
    @Autowired
    private SecurityService securityService;

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Optional<Reservation> getReservationById(Integer id) {
        return reservationRepository.findById(id);
    }

    public Optional<Reservation> getReservationByConfirmationCode(String confirmationCode) {
        return reservationRepository.findByConfirmationCode(confirmationCode);
    }

    public List<Reservation> getReservationsByUserId(Integer userId) {
        securityService.checkUserAccess(userId);
        return reservationRepository.findByUserId(userId);
    }

    public List<Reservation> getReservationsByRestaurantId(Integer restaurantId) {
        return reservationRepository.findByRestaurantId(restaurantId);
    }

    public List<Reservation> getReservationsByRestaurantAndDate(Integer restaurantId, LocalDate date) {
        return reservationRepository.findByRestaurantIdAndDate(restaurantId, date);
    }

    public Reservation createReservation(Reservation reservation) {
        if (reservation.getStatus() == null) {
            reservation.setStatus(ReservationStatus.PENDING);
        }
        Reservation savedReservation = reservationRepository.save(reservation);
        
        if (savedReservation.getUserId() != null) {
            try {
                Restaurant restaurant = restaurantRepository.findById(savedReservation.getRestaurantId()).orElse(null);
                String restaurantName = restaurant != null ? restaurant.getName() : "Restaurant";
                String reservationDate = savedReservation.getReservationDate() + " " + savedReservation.getReservationTime();
                
                asyncNotificationService.sendReservationPending(
                    savedReservation.getUserId(), 
                    savedReservation.getReservationId(),
                    restaurantName,
                    reservationDate
                );
            } catch (Exception e) {
            }
        }
        
        return savedReservation;
    }

    public Reservation updateReservation(Integer id, Reservation reservationDetails) {
        return reservationRepository.findById(id)
                .map(reservation -> {
                    if (reservationDetails.getReservationDate() != null) {
                        reservation.setReservationDate(reservationDetails.getReservationDate());
                    }
                    if (reservationDetails.getReservationTime() != null) {
                        reservation.setReservationTime(reservationDetails.getReservationTime());
                    }
                    if (reservationDetails.getNumberOfGuests() != null) {
                        reservation.setNumberOfGuests(reservationDetails.getNumberOfGuests());
                    }
                    if (reservationDetails.getTableId() != null) {
                        reservation.setTableId(reservationDetails.getTableId());
                    }
                    if (reservationDetails.getStatus() != null) {
                        reservation.setStatus(reservationDetails.getStatus());
                    }
                    if (reservationDetails.getSpecialRequests() != null) {
                        reservation.setSpecialRequests(reservationDetails.getSpecialRequests());
                    }
                    if (reservationDetails.getDurationMinutes() != null) {
                        reservation.setDurationMinutes(reservationDetails.getDurationMinutes());
                    }
                    return reservationRepository.save(reservation);
                })
                .orElseThrow(() -> new RuntimeException("Reservation not found with id: " + id));
    }

    public void deleteReservation(Integer id) {
        reservationRepository.deleteById(id);
    }

    public boolean checkTableAvailability(Integer tableId, LocalDate date) {
        List<Reservation> activeReservations = reservationRepository.findActiveReservationsByTableAndDate(tableId, date);
        return activeReservations.isEmpty();
    }

    public long countAllReservations() {
        return reservationRepository.count();
    }

    public long countByStatus(ReservationStatus status) {
        return reservationRepository.countByStatus(status);
    }

    public long countByDate(LocalDate date) {
        return reservationRepository.countByDate(date);
    }

    public List<ReservationDTO> getAllReservationsDTO() {
        return reservationRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ReservationDTO> getReservationsByStatus(ReservationStatus status) {
        return reservationRepository.findByStatus(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<Reservation> getReservationsByStatusEntity(ReservationStatus status) {
        return reservationRepository.findByStatus(status);
    }

    public List<ReservationDTO> getReservationsByDate(LocalDate date) {
        return reservationRepository.findByDate(date).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ReservationDTO updateReservationStatus(Integer id, ReservationStatusUpdateDTO statusUpdate) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found with id: " + id));
        
        reservation.setStatus(statusUpdate.getStatus());
        
        if (statusUpdate.getReason() != null && !statusUpdate.getReason().isEmpty()) {
            String currentRequests = reservation.getSpecialRequests();
            String updatedRequests = currentRequests != null 
                ? currentRequests + "\n[Status Update: " + statusUpdate.getReason() + "]"
                : "[Status Update: " + statusUpdate.getReason() + "]";
            reservation.setSpecialRequests(updatedRequests);
        }
        
        Reservation updated = reservationRepository.save(reservation);
        
        if (updated.getUserId() != null) {
            try {
                Restaurant restaurant = restaurantRepository.findById(updated.getRestaurantId()).orElse(null);
                String restaurantName = restaurant != null ? restaurant.getName() : "Restaurant";
                
                asyncNotificationService.sendReservationStatusUpdate(
                    updated.getUserId(),
                    updated.getReservationId(),
                    restaurantName,
                    updated.getStatus().name()
                );
            } catch (Exception e) {
            }
        }
        
        return convertToDTO(updated);
    }

    public long countByRestaurantId(Integer restaurantId) {
        return reservationRepository.countByRestaurantId(restaurantId);
    }

    public long countByRestaurantIdAndStatus(Integer restaurantId, ReservationStatus status) {
        return reservationRepository.countByRestaurantIdAndStatus(restaurantId, status);
    }

    public long countByRestaurantIdAndDate(Integer restaurantId, LocalDate date) {
        return reservationRepository.countByRestaurantIdAndDate(restaurantId, date);
    }

    public List<ReservationDTO> getReservationsByRestaurantIdDTO(Integer restaurantId) {
        return reservationRepository.findByRestaurantId(restaurantId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private ReservationDTO convertToDTO(Reservation reservation) {
        ReservationDTO dto = new ReservationDTO();
        dto.setReservationId(reservation.getReservationId());
        dto.setUserId(reservation.getUserId());
        dto.setRestaurantId(reservation.getRestaurantId());
        dto.setTableId(reservation.getTableId());
        dto.setReservationDate(reservation.getReservationDate());
        dto.setReservationTime(reservation.getReservationTime());
        dto.setNumberOfGuests(reservation.getNumberOfGuests());
        dto.setStatus(reservation.getStatus());
        dto.setConfirmationCode(reservation.getConfirmationCode());
        dto.setSpecialRequests(reservation.getSpecialRequests());
        dto.setDurationMinutes(reservation.getDurationMinutes());
        dto.setCreatedAt(reservation.getCreatedAt());
        
        // Populate user name/email (for logged users) or extract guest info from specialRequests
        if (reservation.getUserId() != null) {
            userRepository.findById(reservation.getUserId()).ifPresent(user -> {
                String full = ((user.getFirstName() != null ? user.getFirstName() : "") + " " +
                        (user.getLastName() != null ? user.getLastName() : "")).trim();
                dto.setUserName(full.isEmpty() ? user.getEmail() : full);
                dto.setUserEmail(user.getEmail());
            });
        } else if (reservation.getSpecialRequests() != null && reservation.getSpecialRequests().startsWith("GUEST RESERVATION")) {
            // try to parse guest name and email from stored specialRequests
            String[] lines = reservation.getSpecialRequests().split("\n");
            for (String line : lines) {
                if (line.startsWith("Name: ")) dto.setUserName(line.substring(6).trim());
                else if (line.startsWith("Email: ")) dto.setUserEmail(line.substring(7).trim());
            }
        }

        // Populate restaurant name
        restaurantRepository.findById(reservation.getRestaurantId()).ifPresent(restaurant -> {
            dto.setRestaurantName(restaurant.getName());
        });

        return dto;
    }


    public ReservationDetailDTO createGuestReservation(GuestReservationDTO guestDTO) {
        Restaurant restaurant = restaurantRepository.findById(guestDTO.getRestaurantId())
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        // Find available table if not specified
        Integer tableId = guestDTO.getTableId();
        if (tableId == null) {
            // Get all tables for this restaurant
            java.util.List<gio.backend.entity.Table> tables = tableRepository.findByRestaurantId(guestDTO.getRestaurantId());
            if (tables.isEmpty()) {
                throw new RuntimeException("No tables found for this restaurant");
            }

            // Find first available table that fits the number of guests
            tableId = tables.stream()
                .filter(table -> table.getCapacity() >= guestDTO.getNumberOfGuests())
                .filter(table -> checkTableAvailability(table.getTableId(), guestDTO.getReservationDate()))
                .map(gio.backend.entity.Table::getTableId)
                .findFirst()
                .orElse(tables.get(0).getTableId()); // Fallback to first table if none available
        }

        String guestInfo = String.format("GUEST RESERVATION\nName: %s %s\nEmail: %s\nPhone: %s\n---\n%s",
                guestDTO.getFirstName(),
                guestDTO.getLastName(),
                guestDTO.getEmail(),
                guestDTO.getPhoneNumber(),
                guestDTO.getSpecialRequests() != null ? guestDTO.getSpecialRequests() : "");

        Reservation reservation = new Reservation();
        reservation.setUserId(null);
        reservation.setRestaurantId(guestDTO.getRestaurantId());
        reservation.setTableId(tableId);
        reservation.setReservationDate(guestDTO.getReservationDate());
        reservation.setReservationTime(guestDTO.getReservationTime());
        reservation.setNumberOfGuests(guestDTO.getNumberOfGuests());
        reservation.setDurationMinutes(guestDTO.getDurationMinutes() != null ? guestDTO.getDurationMinutes() : 120);
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setSpecialRequests(guestInfo);
        reservation.setConfirmationCode(generateConfirmationCode());

        Reservation saved = reservationRepository.save(reservation);
        return convertToDetailDTO(saved, guestDTO.getFirstName() + " " + guestDTO.getLastName(),
                                   guestDTO.getEmail(), guestDTO.getPhoneNumber(), true);
    }

    public ReservationDetailDTO getReservationDetailByConfirmationCode(String confirmationCode) {
        Reservation reservation = reservationRepository.findByConfirmationCode(confirmationCode)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        return convertToDetailDTO(reservation);
    }

    public ReservationDetailDTO approveReservation(ReservationApprovalDTO approvalDTO) {
        Reservation reservation = reservationRepository.findById(approvalDTO.getReservationId())
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        ReservationStatus newStatus = ReservationStatus.valueOf(approvalDTO.getStatus());
        reservation.setStatus(newStatus);

        if (approvalDTO.getTableId() != null) {
            reservation.setTableId(approvalDTO.getTableId());
        }

        if (newStatus == ReservationStatus.CANCELLED && approvalDTO.getRejectionReason() != null) {
            reservation.setCancellationReason(approvalDTO.getRejectionReason());
            reservation.setCancelledAt(LocalDateTime.now());
        }

        if (approvalDTO.getAdminNote() != null) {
            String currentRequests = reservation.getSpecialRequests();
            reservation.setSpecialRequests(currentRequests + "\n[Admin Note: " + approvalDTO.getAdminNote() + "]");
        }

        Reservation updated = reservationRepository.save(reservation);
        return convertToDetailDTO(updated);
    }

    public List<ReservationDetailDTO> getPendingReservationsForRestaurant(Integer restaurantId) {
        return reservationRepository.findByRestaurantIdAndStatus(restaurantId, ReservationStatus.PENDING).stream()
                .map(this::convertToDetailDTO)
                .collect(Collectors.toList());
    }

    private ReservationDetailDTO convertToDetailDTO(Reservation reservation) {
        ReservationDetailDTO dto = new ReservationDetailDTO();
        dto.setReservationId(reservation.getReservationId());
        dto.setUserId(reservation.getUserId());
        dto.setRestaurantId(reservation.getRestaurantId());
        dto.setTableId(reservation.getTableId());
        dto.setReservationDate(reservation.getReservationDate());
        dto.setReservationTime(reservation.getReservationTime());
        dto.setDurationMinutes(reservation.getDurationMinutes());
        dto.setNumberOfGuests(reservation.getNumberOfGuests());
        dto.setStatus(reservation.getStatus());
        dto.setConfirmationCode(reservation.getConfirmationCode());
        dto.setCreatedAt(reservation.getCreatedAt());
        dto.setUpdatedAt(reservation.getUpdatedAt());
        dto.setCheckedInAt(reservation.getCheckedInAt());
        dto.setCancelledAt(reservation.getCancelledAt());
        dto.setCancellationReason(reservation.getCancellationReason());

        boolean isGuest = reservation.getUserId() == null;
        dto.setIsGuest(isGuest);

        if (isGuest && reservation.getSpecialRequests() != null && 
            reservation.getSpecialRequests().startsWith("GUEST RESERVATION")) {
            String[] lines = reservation.getSpecialRequests().split("\n");
            for (String line : lines) {
                if (line.startsWith("Name: ")) dto.setFullName(line.substring(6));
                else if (line.startsWith("Email: ")) dto.setEmail(line.substring(7));
                else if (line.startsWith("Phone: ")) dto.setPhoneNumber(line.substring(7));
            }
            int separator = reservation.getSpecialRequests().indexOf("---\n");
            if (separator > 0 && separator + 4 < reservation.getSpecialRequests().length()) {
                dto.setSpecialRequests(reservation.getSpecialRequests().substring(separator + 4));
            }
        } else if (!isGuest) {
            userRepository.findById(reservation.getUserId()).ifPresent(user -> {
                dto.setFullName(user.getFirstName() + " " + user.getLastName());
                dto.setEmail(user.getEmail());
                dto.setPhoneNumber(user.getPhoneNumber());
            });
            dto.setSpecialRequests(reservation.getSpecialRequests());
        }

        restaurantRepository.findById(reservation.getRestaurantId()).ifPresent(restaurant -> {
            dto.setRestaurantName(restaurant.getName());
        });

        if (reservation.getTableId() != null) {
            tableRepository.findById(reservation.getTableId()).ifPresent(table -> {
                dto.setTableNumber(table.getTableNumber());
            });
        }

        return dto;
    }

    private ReservationDetailDTO convertToDetailDTO(Reservation reservation, String fullName, 
                                                    String email, String phone, boolean isGuest) {
        ReservationDetailDTO dto = convertToDetailDTO(reservation);
        dto.setFullName(fullName);
        dto.setEmail(email);
        dto.setPhoneNumber(phone);
        dto.setIsGuest(isGuest);
        return dto;
    }

    private String generateConfirmationCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder code = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }
        return code.toString();
    }
}
