import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Feedback, FeedbackCreateRequest } from '../../shared/models/feedback.model';
import { environment } from '../../../environments/environment'; 

@Injectable({
  providedIn: 'root'
})
export class FeedbackService {
  private apiUrl = `${environment.apiUrl}/feedback`;

  constructor(private http: HttpClient) { }

  getAllFeedback(): Observable<Feedback[]> {
    return this.http.get<Feedback[]>(this.apiUrl);
  }

  getFeedbackById(id: number): Observable<Feedback> {
    return this.http.get<Feedback>(`${this.apiUrl}/${id}`);
  }

  getFeedbackByRestaurant(restaurantId: number): Observable<Feedback[]> {
    return this.http.get<Feedback[]>(`${this.apiUrl}/restaurant/${restaurantId}`);
  }

  getVisibleFeedbackByRestaurant(restaurantId: number): Observable<Feedback[]> {
    return this.http.get<Feedback[]>(`${this.apiUrl}/restaurant/${restaurantId}/visible`);
  }

  getFeedbackByUser(userId: number): Observable<Feedback[]> {
    return this.http.get<Feedback[]>(`${this.apiUrl}/user/${userId}`);
  }

  createFeedback(feedback: FeedbackCreateRequest): Observable<Feedback> {
    return this.http.post<Feedback>(this.apiUrl, feedback);
  }

  updateFeedback(id: number, feedback: Partial<Feedback>): Observable<Feedback> {
    return this.http.put<Feedback>(`${this.apiUrl}/${id}`, feedback);
  }

  deleteFeedback(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}