import { Component, OnInit, ChangeDetectorRef, NgZone } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PaymentService } from '../../core/services/payment.service';

@Component({
  selector: 'app-payment-management',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './payment-management.component.html',
  styleUrls: ['./payment-management.component.css']
})
export class PaymentManagementComponent implements OnInit {
  payments: any[] = [];
  loading: boolean = false;
  error: string = '';

  constructor(private paymentService: PaymentService, private cdr: ChangeDetectorRef, private ngZone: NgZone) {}

  ngOnInit(): void {
    this.loadPayments();
  }

  loadPayments(): void {
    this.loading = true;
    this.paymentService.getAllPayments().subscribe({
      next: (data) => {
        this.ngZone.run(() => {
          this.payments = data;
          this.loading = false;
          this.cdr.markForCheck();
        });
      },
      error: (err: any) => {
        this.ngZone.run(() => {
          this.error = 'Nu s-au putut incarca tranzactiile.';
          this.loading = false;
          this.cdr.markForCheck();
        });
        console.error(err);
      }
    });
  }

  onProcessPayment(id: number): void {
    this.paymentService.processPayment(id).subscribe({
      next: () => this.loadPayments(),
      error: (err: any) => alert('Eroare la procesarea platii.')
    });
  }

  onUpdateStatus(id: number, status: string): void {
    if (!status) return;
    this.paymentService.updatePaymentStatus(id, status).subscribe({
      next: () => this.loadPayments(),
      error: (err: any) => alert('Eroare la actualizarea statusului.')
    });
  }

  onDeletePayment(id: number): void {
    if (confirm('Sigur doriti sa stergeti aceasta inregistrare?')) {
      this.paymentService.deletePayment(id).subscribe({
        next: () => this.loadPayments(),
        error: (err: any) => alert('Eroare la stergere.')
      });
    }
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'COMPLETED': return 'bg-success';
      case 'PENDING': return 'bg-warning text-dark';
      case 'FAILED': return 'bg-danger';
      case 'REFUNDED': return 'bg-info text-dark';
      default: return 'bg-secondary';
    }
  }
}