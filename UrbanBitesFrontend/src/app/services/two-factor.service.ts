import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TwoFaSetup, TwoFaVerify, TwoFaLoginVerify, TwoFaResponse } from '../models/two-fa.model';

@Injectable({
  providedIn: 'root'
})
export class TwoFactorService {
  private apiUrl = 'http://localhost:8080/api/2fa';

  constructor(private http: HttpClient) {}


  setupTwoFa(userId: number): Observable<TwoFaSetup> {
    return this.http.post<TwoFaSetup>(`${this.apiUrl}/setup/${userId}`, {});
  }


  verifyAndEnable2Fa(userId: number, request: TwoFaVerify): Observable<TwoFaResponse> {
    return this.http.post<TwoFaResponse>(`${this.apiUrl}/verify/${userId}`, request);
  }


  verify2FaLogin(userId: number, request: TwoFaLoginVerify): Observable<TwoFaResponse> {
    return this.http.post<TwoFaResponse>(`${this.apiUrl}/verify-login/${userId}`, request);
  }

 
  disable2Fa(userId: number): Observable<TwoFaResponse> {
    return this.http.post<TwoFaResponse>(`${this.apiUrl}/disable/${userId}`, {});
  }


  get2FaStatus(userId: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/status/${userId}`);
  }
}
