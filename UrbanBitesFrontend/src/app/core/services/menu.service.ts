import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, forkJoin, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class MeniuService {
  private apiUrl = environment.apiUrl;
  private menuApiUrl = `${this.apiUrl}/menus`;
  private menuItemApiUrl = `${this.apiUrl}/menu-items`;

  constructor(private http: HttpClient) {}

  createMenu(menu: any): Observable<any> {
    return this.http.post<any>(this.menuApiUrl, menu);
  }

  updateMenu(id: number, menu: any): Observable<any> {
    return this.http.put<any>(`${this.menuApiUrl}/${id}`, menu);
  }

  deleteMenu(id: number): Observable<any> {
    return this.http.delete(`${this.menuApiUrl}/${id}`);
  }

  getActiveMenusByRestaurant(restaurantId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.menuApiUrl}/restaurant/${restaurantId}/active`);
  }

  getMenusByRestaurant(restaurantId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.menuApiUrl}/restaurant/${restaurantId}`);
  }

  getMenuItemsByMenuId(menuId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.menuItemApiUrl}/menu/${menuId}`);
  }

  getAvailableMenuItemsByMenuId(menuId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.menuItemApiUrl}/menu/${menuId}/available`);
  }

  getMenuItemById(id: number): Observable<any> {
    return this.http.get<any>(`${this.menuItemApiUrl}/${id}`);
  }

  createMenuItem(menuItem: any): Observable<any> {
    return this.http.post<any>(this.menuItemApiUrl, menuItem);
  }

  updateMenuItem(id: number, menuItem: any): Observable<any> {
    return this.http.put<any>(`${this.menuItemApiUrl}/${id}`, menuItem);
  }

  deleteMenuItem(id: number): Observable<any> {
    return this.http.delete(`${this.menuItemApiUrl}/${id}`);
  }

  createPreparat(data: any): Observable<any> {
    return this.createMenuItem(data);
  }

  updatePreparat(id: number, data: any): Observable<any> {
    return this.updateMenuItem(id, data);
  }

  stergePreparat(id: number): Observable<void> {
    return this.deleteMenuItem(id);
  }

  getPreparatById(id: number): Observable<any> {
    return this.getMenuItemById(id);
  }

  getPreparateByRestaurant(id: number): Observable<any[]> {
    return this.getAllProductsForRestaurant(id);
  }

  getAllProductsForRestaurant(restaurantId: number): Observable<any[]> {
    return this.getActiveMenusByRestaurant(restaurantId).pipe(
      switchMap(menus => {
        if (!menus || menus.length === 0) return of([]);

        const requests = menus.map(menu =>
          this.getMenuItemsByMenuId(menu.menuId).pipe(
            map(items => items.map(item => ({ ...item, category: menu.name, menuId: menu.menuId || menu.id })))
          )
        );

        return forkJoin(requests).pipe(
          map(results => results.flat())
        );
      })
    );
  }
}