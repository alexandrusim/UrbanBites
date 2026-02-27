import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { ListaRezervariComponent } from './lista-rezervari/lista-rezervari.component';
import { RezervareNouaComponent } from './rezervare-noua/rezervare-noua.component';

const routes: Routes = [
  {
    path: '',
    component: ListaRezervariComponent
  },
  {
    path: 'noua',
    component: RezervareNouaComponent
  },
  {
    path: 'editeaza/:id',
    component: RezervareNouaComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class RezervariRoutingModule { }