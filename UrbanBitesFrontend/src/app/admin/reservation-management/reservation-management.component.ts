import { Component, OnInit, ChangeDetectorRef, NgZone } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ReservationService } from '../../core/services/reservation.service';
import { ReservationDTO } from '../../shared/models/admin.model';

@Component({
  selector: 'app-reservation-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './reservation-management.component.html',
  styleUrls: ['./reservation-management.component.css']
})
export class ReservationManagementComponent implements OnInit {
  reservations: ReservationDTO[] = [];
  filteredReservations: ReservationDTO[] = [];
  loading: boolean = false;
  error: string = '';

  selectedStatus: string = '';
  selectedDate: string = '';

  showReasonModal: boolean = false;
  currentReservation: ReservationDTO | null = null;
  newStatus: string = '';
  statusReason: string = '';

  constructor(private reservationService: ReservationService, private cdr: ChangeDetectorRef, private ngZone: NgZone) {}

  ngOnInit(): void {
    this.loadReservations();
  }

  loadReservations(): void {
    this.loading = true;
    this.reservationService.getAdminReservations().subscribe({
      next: (data: ReservationDTO[]) => {
        this.ngZone.run(() => {
          this.reservations = data;
          this.applyFilters();
          this.loading = false;
          this.cdr.markForCheck();
        });
      },
      error: (err: any) => { 
        this.ngZone.run(() => {
          console.error('Eroare la incarcarea rezervarilor:', err);
          this.error = 'Nu s-au putut încărca rezervările din baza de date.';
          this.loading = false;
          this.cdr.markForCheck();
        });
      }
    });
  }

  applyFilters(): void {
    this.filteredReservations = this.reservations.filter(res => {
      const matchStatus = !this.selectedStatus || res.status === this.selectedStatus;
      const matchDate = !this.selectedDate || res.reservationDate === this.selectedDate;
      return matchStatus && matchDate;
    });
  }

  clearFilters(): void {
    this.selectedStatus = '';
    this.selectedDate = '';
    this.applyFilters();
  }

  openStatusModal(reservation: ReservationDTO, status: string): void {
    this.currentReservation = reservation;
    this.newStatus = status;
    this.statusReason = '';
    this.showReasonModal = true;
  }

  getDisplayName(res: ReservationDTO): string {
    if (res.userName && res.userName.trim()) return res.userName.trim();
    const first = (res.firstName || '').trim();
    const last = (res.lastName || '').trim();
    const full = [first, last].filter(Boolean).join(' ');
    if (full) return full;
    if (res.email && res.email.trim()) return res.email.trim();
    return 'Client necunoscut';
  }

  closeModal(): void {
    this.showReasonModal = false;
    this.currentReservation = null;
  }

  confirmStatusUpdate(): void {
    if (!this.currentReservation) return;

    const payload: any = {
      status: this.newStatus,
      reason: this.statusReason || ''
    };

    this.reservationService.updateAdminStatus(this.currentReservation.reservationId!, payload).subscribe({
      next: () => {
        this.loadReservations();
        this.closeModal();
      },
      error: (err: any) => {
        console.error('Eroare la update status:', err);
        alert('Eroare la actualizarea statusului rezervării.');
      }
    });
  }

  deleteReservation(id: number): void {
    if (confirm('Sigur doriți să ștergeți definitiv această rezervare?')) {
      this.reservationService.deleteAdminReservation(id).subscribe({
        next: () => {
          this.loadReservations();
        },
        error: (err: any) => {
          console.error('Eroare la stergere:', err);
          alert('Eroare la ștergerea rezervării.');
        }
      });
    }
  }


  getStatusLabel(status: string): string {
    const labels: { [key: string]: string } = {
      'PENDING': 'În așteptare',
      'CONFIRMED': 'Confirmată',
      'CANCELLED': 'Anulată',
      'COMPLETED': 'Finalizată',
    };
    return labels[status] || status;
  }

  getStatusBadgeClass(status: string): string {
    switch (status) {
      case 'CONFIRMED': return 'badge-success';
      case 'PENDING': return 'badge-warning';
      case 'CANCELLED': return 'badge-danger';
      case 'COMPLETED': return 'badge-info';
      default: return 'badge-secondary';
    }
  }
}