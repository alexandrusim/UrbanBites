import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Payment, PaymentCreateRequest } from '../../shared/models/payment.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class PaymentService {
  private apiUrl = `${environment.apiUrl}/payments`;

  constructor(private http: HttpClient) { }

  getAllPayments(): Observable<Payment[]> {
    return this.http.get<Payment[]>(this.apiUrl);
  }

  getPaymentById(id: number): Observable<Payment> {
    return this.http.get<Payment>(`${this.apiUrl}/${id}`);
  }

  getPaymentByReservation(reservationId: number): Observable<Payment> {
    return this.http.get<Payment>(`${this.apiUrl}/reservation/${reservationId}`);
  }

  createPayment(payment: PaymentCreateRequest): Observable<Payment> {
    return this.http.post<Payment>(this.apiUrl, payment);
  }

  processPayment(id: number): Observable<Payment> {
    return this.http.put<Payment>(`${this.apiUrl}/${id}/process`, {});
  }

  updatePaymentStatus(id: number, status: string): Observable<Payment> {
    return this.http.put<Payment>(`${this.apiUrl}/${id}/status`, null, {
      params: { status: status }
    });
  }

  deletePayment(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // --- Guest Payment Methods ---
  createGuestPayment(payment: any): Observable<any> {
    return this.http.post<any>(`${environment.apiUrl}/guest/payments`, payment);
  }

  getGuestPaymentByTransaction(transactionId: string): Observable<any> {
    return this.http.get<any>(`${environment.apiUrl}/guest/payments/transaction/${transactionId}`);
  }
}