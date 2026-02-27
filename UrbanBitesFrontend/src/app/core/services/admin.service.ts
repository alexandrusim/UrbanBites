import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DashboardStats, UserDTO, ReservationDTO, ReservationStatusUpdate, ActivityLog } from '../../shared/models/admin.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private baseUrl = environment.apiUrl;
  

  private adminUrl = `${this.baseUrl}/admin`;
  private usersUrl = `${this.baseUrl}/users`; 

  constructor(private http: HttpClient) {}

  getDashboardStats(): Observable<DashboardStats> {
    return this.http.get<DashboardStats>(`${this.adminUrl}/dashboard/stats`);
  }

  getAllUsers(): Observable<UserDTO[]> {
    return this.http.get<UserDTO[]>(this.usersUrl);
  }

  getUserById(id: number): Observable<UserDTO> {
    return this.http.get<UserDTO>(`${this.usersUrl}/${id}`);
  }

  deleteUser(id: number): Observable<void> {
    return this.http.delete<void>(`${this.usersUrl}/${id}`);
  }

  getAllReservations(status?: string, date?: string): Observable<ReservationDTO[]> {
    let params = new HttpParams();
    if (status) params = params.set('status', status);
    if (date) params = params.set('date', date);
    
    return this.http.get<ReservationDTO[]>(`${this.adminUrl}/reservations`, { params });
  }

  updateReservationStatus(id: number, statusUpdate: ReservationStatusUpdate): Observable<ReservationDTO> {
    return this.http.put<ReservationDTO>(`${this.adminUrl}/reservations/${id}/status`, statusUpdate);
  }

  getActivityLogs(): Observable<ActivityLog[]> {
    return this.http.get<ActivityLog[]>(`${this.baseUrl}/activity-logs`);
  }

  getActivityLogsByUser(userId: number): Observable<ActivityLog[]> {
    return this.http.get<ActivityLog[]>(`${this.baseUrl}/activity-logs/user/${userId}`);
  }
}