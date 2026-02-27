import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { take } from 'rxjs/operators';
import { ReservationService } from '../../core/services/reservation.service';
import { ReservationDetailDTO } from '../../shared/models/reservation.model';

@Component({
  selector: 'app-reservation-status-check',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './reservation-status-check.component.html',
  styleUrls: ['./reservation-status-check.component.css']
})
export class ReservationStatusCheckComponent implements OnInit {
  checkForm!: FormGroup;
  loading = false;
  errorMessage = '';
  reservation: ReservationDetailDTO | null = null;

  constructor(
    private fb: FormBuilder,
    private reservationService: ReservationService,
    private route: ActivatedRoute,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.checkForm = this.fb.group({
      confirmationCode: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(8)]]
    });

    // Check if code is provided in query params
    this.route.queryParams.pipe(take(1)).subscribe(params => {
      if (params['code']) {
        this.checkForm.patchValue({ confirmationCode: params['code'] });
        // Use setTimeout to ensure form is fully initialized
        setTimeout(() => this.checkStatus(), 0);
      }
    });
  }

  checkStatus(): void {
    if (this.checkForm.invalid) {
      this.checkForm.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.reservation = null;

    const code = this.checkForm.value.confirmationCode.toUpperCase();
    console.log('=== CHECKING STATUS ===');
    console.log('Code:', code);
    console.log('Loading:', this.loading);

    this.reservationService.getGuestReservationByConfirmationCode(code).subscribe({
      next: (response) => {
        console.log('✓ Success! Response:', response);
        this.loading = false;
        this.reservation = response;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('✗ Error occurred:', error);
        this.loading = false;
        this.errorMessage = error.status === 404
          ? 'Rezervarea cu acest cod nu a fost găsită.'
          : 'A apărut o eroare. Vă rugăm încercați din nou.';
        console.error('Error fetching reservation:', error);
        this.cdr.detectChanges();
      },
      complete: () => {
        console.log('Request completed');
      }
    });
  }

  getStatusClass(status?: string): string {
    switch (status) {
      case 'CONFIRMED': return 'status-confirmed';
      case 'PENDING': return 'status-pending';
      case 'CANCELLED': return 'status-cancelled';
      case 'COMPLETED': return 'status-completed';
      case 'NO_SHOW': return 'status-no-show';
      default: return '';
    }
  }

  getStatusText(status?: string): string {
    switch (status) {
      case 'CONFIRMED': return 'Confirmată';
      case 'PENDING': return 'În așteptare';
      case 'CANCELLED': return 'Anulată';
      case 'COMPLETED': return 'Finalizată';
      case 'NO_SHOW': return 'Neprezentare';
      default: return status || 'Necunoscut';
    }
  }

  getStatusIcon(status?: string): string {
    switch (status) {
      case 'CONFIRMED': return '✓';
      case 'PENDING': return '⏱';
      case 'CANCELLED': return '✕';
      case 'COMPLETED': return '✓';
      case 'NO_SHOW': return '!';
      default: return '?';
    }
  }

  formatDate(dateString?: string): string {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString('ro-RO', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  }

  formatTime(timeString?: string): string {
    if (!timeString) return '';
    return timeString.substring(0, 5);
  }

  reset(): void {
    this.checkForm.reset();
    this.reservation = null;
    this.errorMessage = '';
    this.cdr.detectChanges();
  }
}
