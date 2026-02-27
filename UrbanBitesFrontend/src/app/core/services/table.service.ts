import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { Table, TableCreateRequest } from '../../shared/models/table.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class TableService {
  private apiUrl = `${environment.apiUrl}/tables`;

  constructor(private http: HttpClient) { }

  getAllTables(): Observable<Table[]> {
    return this.http.get<Table[]>(this.apiUrl);
  }

  getTableById(id: number): Observable<Table> {
    return this.http.get<Table>(`${this.apiUrl}/${id}`);
  }

  getTablesByRestaurant(restaurantId: number): Observable<Table[]> {
    return this.http.get<Table[]>(`${this.apiUrl}/restaurant/${restaurantId}`);
  }

  getAvailableTables(restaurantId: number): Observable<Table[]> {
    return this.http.get<Table[]>(`${this.apiUrl}/restaurant/${restaurantId}/available`);
  }

  createTable(table: TableCreateRequest): Observable<Table> {
    return this.http.post<Table>(this.apiUrl, table);
  }

  updateTable(id: number, table: Partial<Table>): Observable<Table> {
    return this.http.put<Table>(`${this.apiUrl}/${id}`, table);
  }

  deleteTable(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}