import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router'; 
import { FormsModule } from '@angular/forms'; 
import { RestaurantService } from '../../core/services/restaurant.service';

@Component({
  standalone: true,
  selector: 'app-restaurant-list',
  templateUrl: './restaurant-list.component.html',
  styleUrls: ['./restaurant-list.component.css'],
  imports: [CommonModule, RouterModule, FormsModule]
})
export class RestaurantListComponent implements OnInit {
  restaurants: any[] = [];
  filteredRestaurants: any[] = [];
  cities: string[] = ['București', 'Cluj', 'Iași', 'Timișoara'];
  cuisineTypes: string[] = ['Italian', 'Romanian', 'Asian', 'Burgers'];
  selectedCity: string = '';
  selectedCuisine: string = '';
  loading: boolean = false;
  error: string | null = null;

  constructor(
    private router: Router,
    private restaurantService: RestaurantService, 
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadRestaurants();
  }

  loadRestaurants(): void {
    this.loading = true;
    this.restaurantService.getAllRestaurants().subscribe({
      next: (data) => {
        this.restaurants = data;
        this.filteredRestaurants = [...data];
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.error = "Eroare la încărcarea datelor din baza de date.";
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  onCityChange(): void { this.applyFilters(); }
  onCuisineChange(): void { this.applyFilters(); }
  
  applyFilters(): void {
    this.filteredRestaurants = this.restaurants.filter(r => {
      const matchCity = !this.selectedCity || r.city === this.selectedCity;
      const matchCuisine = !this.selectedCuisine || r.cuisineType === this.selectedCuisine;
      return matchCity && matchCuisine;
    });
    this.cdr.detectChanges();
  }

  clearFilters(): void {
    this.selectedCity = '';
    this.selectedCuisine = '';
    this.filteredRestaurants = [...this.restaurants];
    this.cdr.detectChanges();
  }

  makeReservation(id: number): void {
    this.router.navigate(['/guest-reservation'], { queryParams: { restaurantId: id } });
  }

  viewRestaurant(id: number): void {
    this.router.navigate(['/restaurants', id]);
  }
}