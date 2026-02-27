import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ReservationService } from '../../core/services/reservation.service';
import { AuthService } from '../../core/services/auth.service';
import { ReservationDetailDTO, ReservationApprovalRequest } from '../../shared/models/reservation.model';

@Component({
  selector: 'app-pending-reservations',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './pending-reservations.component.html',
  styleUrls: ['./pending-reservations.component.css']
})
export class PendingReservationsComponent implements OnInit {
  pendingReservations: ReservationDetailDTO[] = [];
  loading = false;
  errorMessage = '';

  showApproveModal = false;
  showRejectModal = false;
  selectedReservation: ReservationDetailDTO | null = null;
  adminNote = '';
  rejectionReason = '';

  constructor(
    private reservationService: ReservationService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadPendingReservations();
  }

  loadPendingReservations(): void {
    const currentUser = this.authService.getCurrentUser();
    if (!currentUser || !currentUser.restaurantId) {
      this.errorMessage = 'Nu aveți un restaurant asociat.';
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    this.reservationService.getPendingGuestReservations(currentUser.restaurantId).subscribe({
      next: (reservations) => {
        this.loading = false;
        this.pendingReservations = reservations.sort((a, b) => {
          const dateA = new Date(a.createdAt || '').getTime();
          const dateB = new Date(b.createdAt || '').getTime();
          return dateA - dateB;
        });
      },
      error: (error) => {
        this.loading = false;
        this.errorMessage = 'Nu s-au putut încărca rezervările. Vă rugăm încercați din nou.';
        console.error('Error loading pending reservations:', error);
      }
    });
  }

  openApproveModal(reservation: ReservationDetailDTO): void {
    this.selectedReservation = reservation;
    this.adminNote = '';
    this.showApproveModal = true;
  }

  openRejectModal(reservation: ReservationDetailDTO): void {
    this.selectedReservation = reservation;
    this.rejectionReason = '';
    this.showRejectModal = true;
  }

  closeModals(): void {
    this.showApproveModal = false;
    this.showRejectModal = false;
    this.selectedReservation = null;
    this.adminNote = '';
    this.rejectionReason = '';
  }

  confirmApproval(): void {
    if (!this.selectedReservation || !this.selectedReservation.reservationId) {
      return;
    }

    const payload = {
      status: 'CONFIRMED',
      reason: this.adminNote || ''
    };

    this.reservationService.updateAdminStatus(this.selectedReservation.reservationId!, payload).subscribe({
      next: () => {
        this.closeModals();
        this.loadPendingReservations();
        alert('Rezervare confirmată cu succes!');
      },
      error: (error) => {
        alert('Eroare la confirmare: ' + (error.error?.message || 'Eroare necunoscută'));
        console.error('Error approving reservation:', error);
      }
    });
  }

  confirmRejection(): void {
    if (!this.selectedReservation || !this.selectedReservation.reservationId) {
      return;
    }

    if (!this.rejectionReason.trim()) {
      alert('Vă rugăm introduceți un motiv pentru refuz.');
      return;
    }

    const payload = {
      status: 'CANCELLED',
      reason: this.rejectionReason
    };

    this.reservationService.updateAdminStatus(this.selectedReservation.reservationId!, payload).subscribe({
      next: () => {
        this.closeModals();
        this.loadPendingReservations();
        alert('Rezervare respinsă.');
      },
      error: (error) => {
        alert('Eroare la respingere: ' + (error.error?.message || 'Eroare necunoscută'));
        console.error('Error rejecting reservation:', error);
      }
    });
  }

  formatDate(dateString?: string): string {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString('ro-RO', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric'
    });
  }

  formatTime(timeString?: string): string {
    if (!timeString) return '';
    return timeString.substring(0, 5);
  }

  formatDateTime(dateTimeString?: string): string {
    if (!dateTimeString) return '';
    const date = new Date(dateTimeString);
    return date.toLocaleString('ro-RO', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }
}
