import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FeedbackService } from '../../core/services/feedback.service';
import { ReservationService } from '../../core/services/reservation.service';
import { AuthService } from '../../core/services/auth.service';
import { RestaurantService } from '../../core/services/restaurant.service';

@Component({
  selector: 'app-feedback-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './feedback-form.component.html',
  styleUrls: ['./feedback-form.component.css']
})
export class FeedbackFormComponent implements OnInit {
  feedbackForm!: FormGroup;
  reservation: any | null = null;
  restaurants: any[] = [];
  loading: boolean = false;
  error: string = '';
  success: boolean = false;
  stars: number[] = [1, 2, 3, 4, 5];

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private feedbackService: FeedbackService,
    private reservationService: ReservationService,
    private restaurantService: RestaurantService,
    public authService: AuthService
  ) { }

  ngOnInit(): void {
    this.initializeForm();
    this.loadAllRestaurants();

    this.route.queryParams.subscribe((params: any) => {
      if (params['reservationId']) {
        this.loadReservation(+params['reservationId']);
      }
    });
  }

  initializeForm(): void {
    this.feedbackForm = this.fb.group({
      userId: [this.authService.getUserId(), Validators.required],
      restaurantId: [null, Validators.required],
      reservationId: [null],
      rating: [5, [Validators.required, Validators.min(1), Validators.max(5)]],
      foodRating: [5, [Validators.min(1), Validators.max(5)]],
      serviceRating: [5, [Validators.min(1), Validators.max(5)]],
      ambianceRating: [5, [Validators.min(1), Validators.max(5)]],
      valueRating: [5, [Validators.min(1), Validators.max(5)]],
      comment: ['', [Validators.maxLength(1000)]]
    });
  }

  loadAllRestaurants(): void {
    this.restaurantService.getAllRestaurants().subscribe({
      next: (data: any) => {
        this.restaurants = data;
        console.log('Restaurante detectate:', this.restaurants);
      },
      error: (err: any) => {
        this.error = 'Nu s-au putut încărca restaurantele.';
        console.error(err);
      }
    });
  }

  loadReservation(id: number): void {
    this.reservationService.getReservationById(id).subscribe({
      next: (res: any) => {
        this.reservation = res;
        this.feedbackForm.patchValue({
          restaurantId: res.restaurantId,
          reservationId: res.reservationId
        });
      },
      error: (err: any) => console.error('Eroare rezervare:', err)
    });
  }

  setRating(field: string, value: number): void {
    this.feedbackForm.patchValue({ [field]: value });
  }

  onSubmit(): void {
    if (this.feedbackForm.invalid) {
      this.feedbackForm.markAllAsTouched();
      this.error = 'Te rugăm să alegi restaurantul și să dai un rating.';
      return;
    }

    this.loading = true;
    this.feedbackService.createFeedback(this.feedbackForm.value).subscribe({
      next: () => {
        this.success = true;
        this.loading = false;
        setTimeout(() => this.router.navigate(['/restaurants']), 2000);
      },
      error: (err: any) => {
        this.error = 'Eroare la salvare.';
        this.loading = false;
      }
    });
  }

  cancel(): void {
    this.router.navigate(['/restaurants']);
  }
}