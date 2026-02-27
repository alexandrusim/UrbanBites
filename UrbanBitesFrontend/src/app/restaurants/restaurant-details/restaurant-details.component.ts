import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router'; 
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MeniuService } from '../../core/services/menu.service';
import { RestaurantService } from '../../core/services/restaurant.service';

@Component({
  standalone: true,
  selector: 'app-restaurant-details',
  templateUrl: './restaurant-details.component.html',
  styleUrls: ['./restaurant-details.component.css'],
  imports: [CommonModule, RouterModule]
})
export class RestaurantDetailsComponent implements OnInit {
  restaurant: any;
  loading = true;
  activeTab = 'info';
  menuItems: any[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private meniuService: MeniuService,
    private restaurantService: RestaurantService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    const id = this.route.snapshot.params['id'];
    if (id) {
      this.loadRestaurant(+id);
    }
  }

  setActiveTab(tab: string) {
    this.activeTab = tab;
    if (tab === 'menu' && this.restaurant) {
      this.loadMenu();
    }
    this.cdr.detectChanges();
  }

  loadRestaurant(id: number) {
    this.loading = true;
    this.restaurantService.getRestaurantById(id).subscribe({
      next: (data) => {
        this.restaurant = data;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Eroare la încărcarea restaurantului din DB:', err);
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  loadMenu() {
    const resId = this.restaurant?.restaurantId || this.restaurant?.id;
    if (resId) {
      this.meniuService.getPreparateByRestaurant(resId).subscribe({
        next: (data) => {
          this.menuItems = data;
          this.cdr.detectChanges();
        },
        error: (err) => console.error('Eroare la încărcarea meniului din DB:', err)
      });
    }
  }

  makeReservation() {
    const resId = this.restaurant?.restaurantId || this.restaurant?.id;
    if (resId) {
      this.router.navigate(['/guest-reservation'], { queryParams: { restaurantId: resId } });
    }
  }

  goBack() {
    window.history.back();
  }
}