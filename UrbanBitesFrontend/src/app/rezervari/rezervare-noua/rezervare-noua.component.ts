import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Observable } from 'rxjs';
import { ReservationService } from '../../core/services/reservation.service';
import { RestaurantService } from '../../core/services/restaurant.service';
import { TableService } from '../../core/services/table.service';
import { AuthService } from '../../core/services/auth.service';
import { PaymentService } from '../../core/services/payment.service';
import { NotificationService } from '../../core/services/notification.service';
import { Restaurant } from '../../shared/models/restaurant.model';
import { Table } from '../../shared/models/table.model';

@Component({
  selector: 'app-rezervare-noua',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, RouterModule],
  templateUrl: './rezervare-noua.component.html',
  styleUrls: ['./rezervare-noua.component.css']
})
export class RezervareNouaComponent implements OnInit {
  reservationForm!: FormGroup;
  paymentForm!: FormGroup;
  
  restaurants: Restaurant[] = [];
  availableTables: Table[] = [];
  selectedRestaurant: Restaurant | null = null;
  
  loading = false;
  
  processingPayment = false; 
  paymentError = '';
  
  error = '';
  success = false;
  paymentSuccess = false; 
  
  confirmationCode = '';
  newReservationId?: number; 

  timeSlots: string[] = ['12:00', '13:00', '14:00', '18:00', '19:00', '20:00', '21:00'];

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private reservationService: ReservationService,
    private restaurantService: RestaurantService,
    private tableService: TableService,
    private paymentService: PaymentService,
    private notificationService: NotificationService,
    public authService: AuthService, 
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.initializeForm();
    this.initPaymentForm();
    this.loadRestaurants();
    
    this.route.queryParams.subscribe(params => {
      if (params['restaurantId']) {
        this.reservationForm.patchValue({ restaurantId: +params['restaurantId'] });
        this.onRestaurantChange();
      }
    });
  }

  initPaymentForm(): void {
    this.paymentForm = this.fb.group({
      cardHolderName: ['', [Validators.required, Validators.maxLength(100)]],
      cardNumber: ['', [Validators.required, Validators.pattern(/^[0-9]{16}$/)]],
      expiryDate: ['', [Validators.required, Validators.pattern(/^(0[1-9]|1[0-2])\/[0-9]{2}$/)]],
      cvv: ['', [Validators.required, Validators.pattern(/^[0-9]{3,4}$/)]],
      amount: [150.50, [Validators.required, Validators.min(1)]],
      paymentMethod: ['CARD'],
      paymentProvider: ['STRIPE']
    });
  }

  initializeForm(): void {
    const today = new Date().toISOString().split('T')[0];

    this.reservationForm = this.fb.group({
      restaurantId: [null, Validators.required],
      tableId: [null, Validators.required],
      reservationDate: [today, Validators.required],
      reservationTime: ['19:00', Validators.required],
      numberOfGuests: [2, [Validators.required, Validators.min(1)]],
      specialRequests: ['']
    });
  }

  loadRestaurants(): void {
    this.restaurantService.getActiveRestaurants().subscribe({
      next: (data) => {
        this.restaurants = data;
        this.cdr.detectChanges();
      }
    });
  }

  onRestaurantChange(): void {
    const resId = this.reservationForm.get('restaurantId')?.value;
    if (resId) {
      this.selectedRestaurant = this.restaurants.find(r => r.restaurantId === +resId) || null;
      this.loadAvailableTables(+resId);
    }
  }

  loadAvailableTables(id: number): void {
    this.loading = true;
    this.tableService.getAvailableTables(id).subscribe({
      next: (data: any[]) => {
        this.availableTables = data.map(t => ({
          ...t,
          tableId: t.tableId || t.masaId || t.masa_id || t.id,
          capacity: t.capacity || t.numar_locuri || t.numarLocuri || 0
        }));
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Eroare la încărcarea meselor:', err);
        this.loading = false;
      }
    });
  }

  onSubmit(): void {
    if (this.reservationForm.invalid) {
      return;
    }
    
    this.loading = true;
    this.error = '';
    const formData = this.reservationForm.value;

    let request$: Observable<any>;
    let userId: number | null = null;

    if (this.authService.isLoggedIn) {
        const user = this.authService.getCurrentUser();
        userId = user ? Number((user as any).userId || (user as any).id) : null;
        
        if (!userId) {
            this.error = 'Eroare: Nu ești autentificat corect (lipsește User ID).';
            this.loading = false;
            return;
        }

        const cleanRequest = {
            userId: userId,
            restaurantId: Number(formData.restaurantId),
            tableId: Number(formData.tableId),
            reservationDate: formData.reservationDate,
            reservationTime: formData.reservationTime,
            numberOfGuests: Number(formData.numberOfGuests),
            specialRequests: formData.specialRequests || ''
        };

        request$ = this.reservationService.createReservation(cleanRequest);
    } else {
        this.error = 'Trebuie să fii logat.';
        this.loading = false;
        return;
    }

    request$.subscribe({
      next: (res: any) => {
        console.log('✅ Răspuns Backend (Succes):', res);
        this.success = true;
        this.confirmationCode = res.confirmationCode || '';
        this.newReservationId = res.reservationId;
        this.loading = false;

        if (userId) {
            this.sendReservationNotification(userId, formData.reservationDate, formData.reservationTime);
        }

        this.cdr.detectChanges();
      },
      error: (err: any) => {
        console.error('❌ Eroare Backend:', err);
        if (err.error && err.error.message) {
             this.error = 'Eroare server: ' + err.error.message;
        } else {
             this.error = 'Nu s-a putut crea rezervarea.';
        }
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  private sendReservationNotification(userId: number, date: string, time: string): void {
    const messageText = `Rezervarea ta pentru data de ${date} la ora ${time} a fost creată cu succes! Te rugăm să achiți avansul.`;
    
    const notificationPayload = {
      userId: userId,
      message: messageText,
      type: 'IN_APP',
      isRead: false
    };
    this.notificationService.createNotification(notificationPayload).subscribe({
      next: () => console.log('🔔 Notificare rezervare trimisă!'),
      error: (err) => console.error('⚠️ Eroare notificare:', err)
    });
  }

  onPaymentSubmit(): void {
    if (this.paymentForm.invalid) {
      Object.keys(this.paymentForm.controls).forEach(key => {
        this.paymentForm.get(key)?.markAsTouched();
      });
      return;
    }

    if (!this.newReservationId) {
      this.error = 'Nu s-a putut găsi ID-ul rezervării.';
      return;
    }

    this.processingPayment = true;
    this.paymentError = '';

    const user = this.authService.getCurrentUser();
    const userId = user ? ((user as any).userId || (user as any).id) : 0;

    const paymentRequest = {
      reservationId: this.newReservationId,
      userId: userId, 
      amount: this.paymentForm.value.amount, 
      currency: 'RON',
      paymentMethod: 'CARD',
      paymentProvider: 'Stripe',
      cardHolderName: this.paymentForm.value.cardHolderName,
      cardNumber: this.paymentForm.value.cardNumber,
      expiryDate: this.paymentForm.value.expiryDate,
      cvv: this.paymentForm.value.cvv
    };

    this.paymentService.createGuestPayment(paymentRequest).subscribe({
      next: (paymentResponse) => {
          this.paymentSuccess = true;
          this.processingPayment = false;
          
          if (userId) {
             const payPayload = {
               userId: userId,
               message: `Plata pentru rezervarea #${this.newReservationId} a fost confirmată. Vă așteptăm!`,
               type: 'SUCCESS',
               isRead: false
             };
             this.notificationService.createNotification(payPayload).subscribe({
                next: () => console.log('🔔 Notificare plată trimisă!'),
                error: (err) => console.error('⚠️ Eroare notificare plată:', err)
             });
          }

          this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Eroare la plată:', err);
        this.paymentError = 'Plata a fost respinsă. Verifică datele cardului.';
        this.processingPayment = false;
        this.cdr.detectChanges();
      }
    });
  }

  cancel(): void {
    this.router.navigate(['/restaurants']);
  }
}