import { Component, OnInit } from '@angular/core';
import { ReservationService } from '../../core/services/reservation.service';
import { AuthService } from '../../core/services/auth.service';
import { Reservation } from '../../shared/models/reservation.model';

@Component({
  selector: 'app-lista-rezervari',
  templateUrl: './lista-rezervari.component.html',
  styleUrls: ['./lista-rezervari.component.css']
})
export class ListaRezervariComponent implements OnInit {
  reservations: Reservation[] = [];
  isLoading = true;
  userId: number | null = null;

  constructor(private rs: ReservationService, private as: AuthService) {}

  ngOnInit(): void {
    const user = this.as.getCurrentUser();
    this.userId = user ? ((user as any).userId || (user as any).id) : null;
    if (this.userId) this.loadReservations();
  }

  loadReservations(): void {
    this.isLoading = true;
    if (!this.userId) return;
    this.rs.getReservationsByUser(this.userId).subscribe({
      next: (data) => {
        this.reservations = data;
        this.isLoading = false;
      },
      error: () => this.isLoading = false
    });
  }
}