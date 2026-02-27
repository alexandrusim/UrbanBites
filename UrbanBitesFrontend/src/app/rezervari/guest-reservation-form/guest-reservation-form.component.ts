import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, ActivatedRoute, RouterModule } from '@angular/router';
import { ReservationService } from '../../core/services/reservation.service';
import { RestaurantService } from '../../core/services/restaurant.service';
import { PaymentService } from '../../core/services/payment.service';
import { AuthService } from '../../core/services/auth.service';
import { GuestReservationRequest } from '../../shared/models/reservation.model';
import { Restaurant } from '../../shared/models/restaurant.model';

@Component({
  selector: 'app-guest-reservation-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './guest-reservation-form.component.html', 
  styleUrls: ['./guest-reservation-form.component.css']
})
export class GuestReservationFormComponent implements OnInit {
  reservationForm!: FormGroup;
  paymentForm!: FormGroup;
  
  loading = false;
  errorMessage = '';
  confirmationCode = '';
  showSuccess = false; 
  
  restaurants: Restaurant[] = [];
  loadingRestaurants = true;

  showPayment = false; 
  paymentLoading = false;
  paymentError = '';
  createdReservationId: number | null | undefined = null;
  transactionId = '';

  timeSlots = [
    '12:00:00', '13:00:00', '14:00:00',
    '18:00:00', '19:00:00', '20:00:00', '21:00:00'
  ];

  constructor(
    private fb: FormBuilder,
    private reservationService: ReservationService,
    private restaurantService: RestaurantService,
    private paymentService: PaymentService,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    if (this.authService.isLoggedIn) {
      console.log('Utilizator logat pe ruta Guest. Redirecționare...');
      const restaurantId = this.route.snapshot.queryParams['restaurantId'];
      
      if (restaurantId) {
        this.router.navigate(['/rezervari/noua'], { queryParams: { restaurantId: restaurantId } });
      } else {
        this.router.navigate(['/rezervari/noua']);
      }
      return;
    }

    this.initForm();
    this.initPaymentForm();
    this.loadRestaurants();

    this.route.queryParams.subscribe(params => {
      if (params['restaurantId']) {
        const restaurantId = parseInt(params['restaurantId']);
        this.reservationForm.patchValue({ restaurantId });
      }
    });
  }

  initForm(): void {
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    const tomorrowString = tomorrow.toISOString().split('T')[0];

    this.reservationForm = this.fb.group({
      firstName: ['', [Validators.required, Validators.maxLength(100)]],
      lastName: ['', [Validators.required, Validators.maxLength(100)]],
      email: ['', [Validators.required, Validators.email]],
      phoneNumber: ['', [Validators.required, Validators.pattern(/^[+]?[0-9]{10,15}$/)]],
      restaurantId: [null, Validators.required],
      reservationDate: [tomorrowString, Validators.required],
      reservationTime: ['', Validators.required],
      numberOfGuests: [2, [Validators.required, Validators.min(1), Validators.max(50)]],
      specialRequests: [''],
      durationMinutes: [120]
    });
  }

  initPaymentForm(): void {
    this.paymentForm = this.fb.group({
      cardHolderName: ['', [Validators.required, Validators.maxLength(100)]],
      cardNumber: ['', [Validators.required, Validators.pattern(/^[0-9]{16}$/)]],
      expiryDate: ['', [Validators.required, Validators.pattern(/^(0[1-9]|1[0-2])\/[0-9]{2}$/)]],
      cvv: ['', [Validators.required, Validators.pattern(/^[0-9]{3,4}$/)]],
      amount: [50, [Validators.required, Validators.min(1)]],
      paymentMethod: ['CARD'],
      paymentProvider: ['STRIPE']
    });
  }

  loadRestaurants(): void {
    this.loadingRestaurants = true;
    this.reservationForm.get('restaurantId')?.disable();

    this.restaurantService.getActiveRestaurants().subscribe({
      next: (restaurants) => {
        this.restaurants = restaurants;
        this.loadingRestaurants = false;
        this.reservationForm.get('restaurantId')?.enable();
      },
      error: (error) => {
        console.error('Error loading restaurants:', error);
        this.restaurantService.getAllRestaurants().subscribe({
          next: (restaurants) => {
            this.restaurants = restaurants;
            this.loadingRestaurants = false;
            this.reservationForm.get('restaurantId')?.enable();
          },
          error: () => {
             this.loadingRestaurants = false;
             this.reservationForm.get('restaurantId')?.enable();
          }
        });
      }
    });
  }

  onSubmit(): void {
    if (this.reservationForm.invalid) {
      Object.keys(this.reservationForm.controls).forEach(key => {
        this.reservationForm.get(key)?.markAsTouched();
      });
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    const formValue = this.reservationForm.value;
    const guestReservation: GuestReservationRequest = {
      firstName: formValue.firstName,
      lastName: formValue.lastName,
      email: formValue.email,
      phoneNumber: formValue.phoneNumber,
      restaurantId: parseInt(formValue.restaurantId),
      reservationDate: formValue.reservationDate,
      reservationTime: formValue.reservationTime,
      numberOfGuests: formValue.numberOfGuests,
      specialRequests: formValue.specialRequests || undefined,
      durationMinutes: formValue.durationMinutes
    };

    this.reservationService.createGuestReservation(guestReservation).subscribe({
      next: (response) => {
        this.loading = false;
        this.confirmationCode = response.confirmationCode || '';
        this.createdReservationId = response.reservationId;
        this.showPayment = true; 
        this.cdr.detectChanges();
      },
      error: (error) => {
        this.loading = false;
        if (error.error && typeof error.error === 'object') {
          this.errorMessage = error.error.message || JSON.stringify(error.error);
        } else {
          this.errorMessage = 'A apărut o eroare la crearea rezervării.';
        }
        this.cdr.detectChanges();
      }
    });
  }

  onPaymentSubmit(): void {
    if (this.paymentForm.invalid) {
      Object.keys(this.paymentForm.controls).forEach(key => {
        this.paymentForm.get(key)?.markAsTouched();
      });
      return;
    }

    this.paymentLoading = true;
    this.paymentError = '';

    const paymentData = {
      reservationId: this.createdReservationId,
      amount: this.paymentForm.value.amount,
      currency: 'RON',
      paymentMethod: this.paymentForm.value.paymentMethod,
      paymentProvider: this.paymentForm.value.paymentProvider,
      cardHolderName: this.paymentForm.value.cardHolderName,
      cardNumber: this.paymentForm.value.cardNumber,
      expiryDate: this.paymentForm.value.expiryDate,
      cvv: this.paymentForm.value.cvv
    };

    this.paymentService.createGuestPayment(paymentData).subscribe({
      next: (response) => {
        this.paymentLoading = false;
        this.transactionId = response.transactionId || '';
        this.showPayment = false;
        this.showSuccess = true; 
        this.paymentForm.reset();
      },
      error: (error) => {
        this.paymentLoading = false;
        this.paymentError = 'Plata a eșuat. Vă rugăm verificați datele cardului.';
      }
    });
  }


  cancel(): void {
    this.router.navigate(['/']);
  }

  skipPayment(): void {
    this.showPayment = false;
    this.showSuccess = true;
  }

  checkStatus(): void {
    if (this.confirmationCode) {
      this.router.navigate(['/reservation-status'], { queryParams: { code: this.confirmationCode } });
    }
  }

  makeAnotherReservation(): void {
    this.showSuccess = false;
    this.confirmationCode = '';
    this.initForm();
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.reservationForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched));
  }

  getFieldError(fieldName: string): string {
    const field = this.reservationForm.get(fieldName);
    if (field?.hasError('required')) return 'Acest câmp este obligatoriu';
    if (field?.hasError('email')) return 'Email invalid';
    if (field?.hasError('pattern')) return 'Format invalid';
    if (field?.hasError('min')) return 'Valoare prea mică';
    if (field?.hasError('max')) return 'Valoare prea mare';
    return '';
  }

  getMinDate(): string {
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    return tomorrow.toISOString().split('T')[0];
  }
}