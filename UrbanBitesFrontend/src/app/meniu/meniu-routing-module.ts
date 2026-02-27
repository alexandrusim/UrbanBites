import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { VizualizareMeniuComponent } from './vizualizare-meniu/vizualizare-meniu';
import { AdministrareMeniuComponent } from './administrare-meniu/administrare-meniu'; 


import { MenuManagementComponent } from '../admin/menu-management/menu-management.component'; 

import { adminGuard } from '../core/guards/auth.guard';

const routes: Routes = [
  { path: '', redirectTo: 'view', pathMatch: 'full' }, 
  
  // Public view
  { path: 'view', component: VizualizareMeniuComponent },
  
  // Restaurant Admin view
  { 
    path: 'manage', 
    component: MenuManagementComponent, 
    canActivate: [adminGuard] 
  },
  { 
    path: 'manage/new', 
    component: AdministrareMeniuComponent, 
    canActivate: [adminGuard] 
  },
  { 
    path: 'manage/edit/:id', 
    component: AdministrareMeniuComponent, 
    canActivate: [adminGuard] 
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class MeniuRoutingModule { }