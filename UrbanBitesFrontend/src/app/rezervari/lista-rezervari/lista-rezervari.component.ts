import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common'; 
import { RouterModule } from '@angular/router'; 
import { ReservationService } from '../../core/services/reservation.service';
import { AuthService } from '../../core/services/auth.service';
import { Reservation } from '../../shared/models/reservation.model';

@Component({
  selector: 'app-lista-rezervari',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './lista-rezervari.component.html',
  styleUrls: ['./lista-rezervari.component.css']
})
export class ListaRezervariComponent implements OnInit {
  reservations: Reservation[] = [];
  isLoading = true;
  userId: number | null = null;

  constructor(
    private reservationService: ReservationService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    console.log('--- Initializare Lista Rezervari ---');
    
    const user = this.authService.getCurrentUser();
    console.log('User curent din AuthService:', user);

    if (user) {
      this.userId = (user as any).userId || (user as any).id || (user as any).user_id;
      console.log('ID Utilizator extras:', this.userId);
    }

    if (this.userId) {
      this.loadReservations();
    } else {
      console.warn('Nu s-a găsit niciun ID de utilizator. Nu pot încărca rezervările.');
      this.isLoading = false;
    }
  }

  loadReservations(): void {
    this.isLoading = true;
    if (!this.userId) return;

    console.log(`Cerere API rezervări pentru user ID: ${this.userId}`);

    this.reservationService.getReservationsByUser(this.userId).subscribe({
      next: (data) => {
        console.log('Rezervări primite de la server:', data);
        
        this.reservations = data.sort((a, b) => {
          const dateA = new Date(a.reservationDate + 'T' + a.reservationTime);
          const dateB = new Date(b.reservationDate + 'T' + b.reservationTime);
          return dateB.getTime() - dateA.getTime();
        });
        
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Eroare la încărcarea rezervărilor:', err);
        this.isLoading = false;
      }
    });
  }

  cancelReservation(id: number | undefined): void {
    if (!id) return;
    
    if (confirm('Ești sigur că vrei să anulezi această rezervare?')) {
      this.reservationService.updateStatus(id, 'CANCELLED', 'Anulat de client').subscribe({
        next: () => {
          console.log('Rezervare anulată cu succes ID:', id);
          this.loadReservations(); 
        },
        error: (err) => {
          console.error('Eroare la anularea rezervării:', err);
          alert('Nu s-a putut anula rezervarea.');
        }
      });
    }
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'CONFIRMED': return 'status-confirmed';
      case 'PENDING': return 'status-pending';
      case 'CANCELLED': return 'status-cancelled';
      case 'COMPLETED': return 'status-completed';
      case 'NO_SHOW': return 'status-cancelled'; 
      default: return '';
    }
  }

  getStatusLabel(status: string): string {
    const labels: {[key: string]: string} = {
      'CONFIRMED': 'Confirmată',
      'PENDING': 'În așteptare',
      'CANCELLED': 'Anulată',
      'COMPLETED': 'Finalizată',
      'NO_SHOW': 'Neprezentat',
      'REJECTED': 'Respinsă'
    };
    return labels[status] || status;
  }
}