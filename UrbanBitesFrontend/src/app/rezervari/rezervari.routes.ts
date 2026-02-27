import { Routes } from '@angular/router';
import { Rezervari } from './rezervari'; 
import { RezervareNouaComponent } from './rezervare-noua/rezervare-noua.component';
import { RezervareFormular } from './rezervare-formular/rezervare-formular';

export const REZERVARI_ROUTES: Routes = [
  {
    path: '', 
    component: Rezervari 
  },
  {
    path: 'noua', 
    component: RezervareNouaComponent 
  },
  {
    path: 'editeaza/:id', 
    component: RezervareFormular 
  }
];