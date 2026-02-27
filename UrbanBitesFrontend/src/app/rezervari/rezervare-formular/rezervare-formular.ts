import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ReservationService } from '../../core/services/reservation.service'; 
import { switchMap } from 'rxjs/operators';
import { of } from 'rxjs';

@Component({
  selector: 'app-rezervare-formular',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, RouterModule],
  templateUrl: './rezervare-formular.component.html',
  styleUrl: './rezervare-formular.component.css'
})
export class RezervareFormular implements OnInit {
  rezervareForm!: FormGroup;
  esteEditare: boolean = false;
  rezervareId!: number;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private reservationService: ReservationService 
  ) { }

  ngOnInit(): void {
    this.rezervareForm = this.fb.group({
      masa_id: [null, Validators.required],
      interval_orar: ['', Validators.required],
      numar_persoane: [1, [Validators.required, Validators.min(1), Validators.max(10)]],
      user_id: [1], 
      restaurant_id: [1] 
    });

    this.verificaModEditare();
  }

  verificaModEditare(): void {
    this.route.params.pipe(
      switchMap(params => {
        const id = +params['id'];
        if (id) {
          this.esteEditare = true;
          this.rezervareId = id;
          return this.reservationService.getReservationById(id);
        }
        return of(null);
      })
    ).subscribe(rezervare => {
      if (rezervare) this.rezervareForm.patchValue(rezervare);
    });
  }

  onSubmit(): void {
    if (this.rezervareForm.valid) {
      const date = this.rezervareForm.value;
      
      if (this.esteEditare) {
        this.reservationService.updateReservation(this.rezervareId, date).subscribe(() => {
          this.router.navigate(['/rezervari']);
        });
      } else {
        this.reservationService.createReservation(date).subscribe(() => {
          this.router.navigate(['/rezervari']);
        });
      }
    }
  }
}