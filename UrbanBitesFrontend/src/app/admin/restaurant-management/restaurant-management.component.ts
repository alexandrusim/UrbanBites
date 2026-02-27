import { Component, OnInit, ChangeDetectorRef, NgZone } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { RestaurantService } from '../../core/services/restaurant.service';
import { Restaurant } from '../../shared/models/restaurant.model';

@Component({
  selector: 'app-restaurant-management',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './restaurant-management.component.html',
  styleUrls: ['./restaurant-management.component.css']
})
export class RestaurantManagementComponent implements OnInit {
  restaurants: Restaurant[] = [];
  loading = false;
  showModal = false;
  isEditMode = false;
  currentRestaurant: Restaurant = this.getEmptyRestaurant();

  constructor(
    private restaurantService: RestaurantService,
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone
  ) {}

  ngOnInit(): void {
    this.loadRestaurants();
  }

  loadRestaurants(): void {
    this.loading = true;
    
    this.restaurantService.getAllRestaurants().subscribe({
      next: (data: Restaurant[]) => {
        this.ngZone.run(() => {
          console.log('Date primite:', data);
          this.restaurants = data;
          this.loading = false;
          this.cdr.markForCheck();
        });
      },
      error: (err: any) => {
        this.ngZone.run(() => {
          console.error('Error loading restaurants:', err);
          this.loading = false;
          this.cdr.markForCheck();
        });
      }
    });
  }
  
  openCreateModal(): void {
    this.isEditMode = false;
    this.currentRestaurant = this.getEmptyRestaurant();
    this.showModal = true;
  }

  openEditModal(restaurant: Restaurant): void {
    this.isEditMode = true;
    this.currentRestaurant = { ...restaurant };
    this.showModal = true;
  }

  closeModal(): void {
    this.showModal = false;
    this.currentRestaurant = this.getEmptyRestaurant();
  }

  saveRestaurant(): void {
    const restaurantData = { ...this.currentRestaurant };

    if (restaurantData.priceRange) {
      const priceMap: { [key: string]: number } = {
        '$': 1, '$$': 2, '$$$': 3, '$$$$': 4
      };
      const val = String(restaurantData.priceRange);
      const priceValue = priceMap[val] || parseInt(val, 10);
      (restaurantData as any).priceRange = priceValue;
    }

    if (this.isEditMode && this.currentRestaurant.restaurantId) {
      this.restaurantService.updateRestaurant(this.currentRestaurant.restaurantId, restaurantData)
        .subscribe({
          next: () => {
            this.loadRestaurants();
            this.closeModal();
          },
          error: (err) => console.error(err)
        });
    } else {
      this.restaurantService.createRestaurant(restaurantData as any).subscribe({
        next: () => {
          this.loadRestaurants();
          this.closeModal();
        },
        error: (err) => console.error(err)
      });
    }
  }

  deleteRestaurant(id: number): void {
    if (confirm('Sigur doriți să ștergeți acest restaurant?')) {
      this.restaurantService.deleteRestaurant(id).subscribe({
        next: () => this.loadRestaurants(),
        error: (err) => console.error(err)
      });
    }
  }

  toggleActive(restaurant: Restaurant): void {
    if (restaurant.restaurantId) {
      const updated = { ...restaurant, isActive: !restaurant.isActive };
      this.restaurantService.updateRestaurant(restaurant.restaurantId, updated).subscribe({
        next: () => this.loadRestaurants(),
        error: (err) => console.error(err)
      });
    }
  }

  private getEmptyRestaurant(): Restaurant {
    return {
      name: '', address: '', city: '', phoneNumber: '', country: 'România', isActive: true
    };
  }
}