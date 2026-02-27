import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RezervariRoutingModule } from './rezervari-routing-module';

import { ListaRezervariComponent } from './lista-rezervari/lista-rezervari.component';
import { RezervareNouaComponent } from './rezervare-noua/rezervare-noua.component'; 

@NgModule({
  declarations: [
  ],
  imports: [
    CommonModule,
    RezervariRoutingModule,
    ListaRezervariComponent,
    RezervareNouaComponent
  ]
})
export class RezervariModule { }