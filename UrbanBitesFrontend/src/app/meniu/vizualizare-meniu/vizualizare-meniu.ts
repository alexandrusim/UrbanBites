import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { MeniuService } from '../../core/services/menu.service';
import { AuthService } from '../../core/services/auth.service';
import { Router } from '@angular/router';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-vizualizare-meniu',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './vizualizare-meniu.component.html',
  styleUrl: './vizualizare-meniu.component.css'
})
export class VizualizareMeniuComponent implements OnInit {
  allMenuItems: any[] = []; 
  filteredItems: any[] = []; 
  categories: string[] = ['Toate', 'Main Course', 'Pasta', 'Salads', 'Dessert'];
  selectedCategory: string = 'Toate';
  loading: boolean = true;
  restaurantId: number | null = null;

  constructor(
    private menuService: MeniuService,
    private route: ActivatedRoute,
    private cdr: ChangeDetectorRef,
    public authService: AuthService,
    public router: Router
  ) {}

  ngOnInit(): void {
    console.log('Componenta VizualizareMeniu s-a inițializat');
    
    let currentRoute: ActivatedRoute | null = this.route;
    let id = null;

    while (currentRoute) {
      id = currentRoute.snapshot.paramMap.get('id');
      if (id) break;
      currentRoute = currentRoute.parent;
    }

    if (id) {
      this.restaurantId = +id;
      console.log('ID Restaurant găsit:', this.restaurantId);
      this.loadMenu(this.restaurantId);
    } else {
      console.error('Nu s-a găsit ID-ul restaurantului în URL');
      this.loading = false;
      this.cdr.detectChanges();
    }
  }

  loadMenu(id: number): void {
    this.loading = true;

    this.menuService.getActiveMenusByRestaurant(id).subscribe({
      next: (menus: any[]) => {
        console.log('Meniuri primite:', menus);

        if (menus && menus.length > 0) {
          const requests = menus.map((menu: any) => {
            if (this.authService.isRestaurantAdmin() || this.authService.isSystemAdmin()) {
              return this.menuService.getMenuItemsByMenuId(menu.menuId);
            }
            return this.menuService.getAvailableMenuItemsByMenuId(menu.menuId);
          });

          forkJoin(requests).subscribe({
            next: (results: any[]) => {
              this.allMenuItems = results.flat();
              this.filteredItems = [...this.allMenuItems]; 
              
              console.log('Toate produsele încărcate:', this.allMenuItems);
              this.loading = false;
              this.cdr.detectChanges();
            },
            error: (err) => {
              console.error('Eroare la încărcare produse:', err);
              this.loading = false;
              this.cdr.detectChanges();
            }
          });
        } 
        else {
          console.warn('Nu există meniuri active pentru acest restaurant.');
          this.allMenuItems = [];
          this.filteredItems = [];
          this.loading = false; 
          this.cdr.detectChanges();
        }
      },
      error: (err: any) => {
        console.error('Eroare la API Meniu:', err);
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  filterByCategory(category: string): void {
    this.selectedCategory = category;
    if (category === 'Toate') {
      this.filteredItems = this.allMenuItems;
    } else {
      this.filteredItems = this.allMenuItems.filter(item => 
        (item.category === category) || (item.categorie === category)
      );
    }
    this.cdr.detectChanges();
  }
}