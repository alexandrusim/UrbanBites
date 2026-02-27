import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Reservation, ReservationCreateRequest, ReservationUpdateRequest, GuestReservationRequest, ReservationDetailDTO, ReservationApprovalRequest } from '../../shared/models/reservation.model';
import { ReservationDTO } from '../../shared/models/admin.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ReservationService {
  private apiUrl = `${environment.apiUrl}/reservations`;
  private adminApiUrl = `${environment.apiUrl}/admin/reservations`;
  private guestApiUrl = `${environment.apiUrl}/guest`;

  constructor(private http: HttpClient) { }

  getAllReservations(): Observable<ReservationDTO[]> {
    return this.http.get<ReservationDTO[]>(this.apiUrl);
  }

  getAdminReservations(): Observable<ReservationDTO[]> {
    return this.http.get<ReservationDTO[]>(this.adminApiUrl);
  }

  getReservationById(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`);
  }

  getReservationByConfirmationCode(code: string): Observable<Reservation> {
    return this.http.get<Reservation>(`${this.apiUrl}/confirmation/${code}`);
  }

  getReservationsByUser(userId: number): Observable<Reservation[]> {
    return this.http.get<Reservation[]>(`${this.apiUrl}/user/${userId}`);
  }

  getReservationsByRestaurant(restaurantId: number): Observable<Reservation[]> {
    return this.http.get<Reservation[]>(`${this.apiUrl}/restaurant/${restaurantId}`);
  }

  createReservation(reservation: ReservationCreateRequest): Observable<Reservation> {
    return this.http.post<Reservation>(this.apiUrl, reservation);
  }

  updateReservation(id: number, reservation: ReservationUpdateRequest): Observable<Reservation> {
    return this.http.put<Reservation>(`${this.apiUrl}/${id}`, reservation);
  }

  updateStatus(id: number, status: string, reason?: string): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}/status`, null, {
      params: {
        status: status,
        reason: reason || ''
      }
    });
  }

  updateAdminStatus(id: number, payload: any): Observable<any> {
    return this.http.put(`${environment.apiUrl}/admin/reservations/${id}/status`, payload);
  }

  deleteReservation(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  deleteAdminReservation(id: number): Observable<void> {
    return this.http.delete<void>(`${environment.apiUrl}/admin/reservations/${id}`);
  }


  createGuestReservation(guestReservation: GuestReservationRequest): Observable<ReservationDetailDTO> {
    return this.http.post<ReservationDetailDTO>(`${this.guestApiUrl}/reservations`, guestReservation);
  }

  getGuestReservationByConfirmationCode(code: string): Observable<ReservationDetailDTO> {
    return this.http.get<ReservationDetailDTO>(`${this.guestApiUrl}/reservations/confirmation/${code}`);
  }

  getPendingGuestReservations(restaurantId: number): Observable<ReservationDetailDTO[]> {
    return this.http.get<ReservationDetailDTO[]>(`${this.guestApiUrl}/reservations/restaurant/${restaurantId}/pending`);
  }

  approveReservation(approval: ReservationApprovalRequest): Observable<ReservationDetailDTO> {
    return this.http.put<ReservationDetailDTO>(`${this.guestApiUrl}/reservations/approve`, approval);
  }
}