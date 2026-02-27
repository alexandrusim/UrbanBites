import { Routes } from '@angular/router';
import { authGuard, adminGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'restaurants',
    pathMatch: 'full'
  },

  {
    path: 'restaurants',
    children: [
      {
        path: '',
        loadComponent: () => import('./restaurants/restaurant-list/restaurant-list.component').then(m => m.RestaurantListComponent)
      },
      {
        path: ':id',
        children: [
          {
            path: '',
            loadComponent: () => import('./restaurants/restaurant-details/restaurant-details.component').then(m => m.RestaurantDetailsComponent)
          },
          {
            path: 'meniu', 
            loadChildren: () => import('./meniu/meniu-module').then(m => m.MeniuModule)
          }
        ]
      }
    ]
  },

  {
    path: 'login',
    loadComponent: () => import('./auth/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'register',
    loadComponent: () => import('./auth/register/register.component').then(m => m.RegisterComponent)
  },
  {
    path: 'setup-2fa',
    canActivate: [authGuard],
    loadComponent: () => import('./auth/setup-2fa/setup-2fa.component').then(m => m.Setup2faComponent)
  },
  {
    path: 'verify-2fa',
    loadComponent: () => import('./auth/verify-2fa/verify-2fa.component').then(m => m.Verify2faComponent)
  },
  {
    path: 'profile',
    canActivate: [authGuard],
    loadComponent: () => import('./profile/profile.component').then(m => m.ProfileComponent)
  },

  { 
    path: 'rezervari', 
    canActivate: [authGuard], 
    loadChildren: () => import('./rezervari/rezervari-module').then(m => m.RezervariModule) 
  },
  {
    path: 'notifications',
    canActivate: [authGuard],
    loadComponent: () => import('./notifications/notification-list/notification-list.component').then(m => m.NotificationListComponent)
  },

  {
    path: 'guest-reservation',
    loadComponent: () => import('./rezervari/guest-reservation-form/guest-reservation-form.component').then(m => m.GuestReservationFormComponent)
  },
  {
    path: 'reservation-status',
    loadComponent: () => import('./rezervari/reservation-status-check/reservation-status-check.component').then(m => m.ReservationStatusCheckComponent)
  },
  {
    path: 'feedback',
    canActivate: [authGuard],
    loadComponent: () => import('./feedback/feedback-form/feedback-form.component').then(m => m.FeedbackFormComponent)
  },

  {
    path: 'admin',
    canActivate: [adminGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      {
        path: 'dashboard',
        loadComponent: () => import('./admin/admin-dashboard/admin-dashboard.component').then(m => m.AdminDashboardComponent)
      },
      {
        path: 'users',
        loadComponent: () => import('./admin/user-management/user-management.component').then(m => m.UserManagementComponent)
      },
      {
        path: 'restaurants',
        loadComponent: () => import('./admin/restaurant-management/restaurant-management.component').then(m => m.RestaurantManagementComponent)
      },
      
      {
        path: 'menu',
        loadComponent: () => import('./admin/menu-management/menu-management.component').then(m => m.MenuManagementComponent)
      },
      {
        path: 'menu/new',
        loadComponent: () => import('./meniu/administrare-meniu/administrare-meniu').then(m => m.AdministrareMeniuComponent)
      },
      {
        path: 'menu/edit/:id',
        loadComponent: () => import('./meniu/administrare-meniu/administrare-meniu').then(m => m.AdministrareMeniuComponent)
      },
      {
        path: 'menu/:id', 
        loadComponent: () => import('./admin/menu-management/menu-management.component').then(m => m.MenuManagementComponent)
      },

      {
        path: 'tables',
        loadComponent: () => import('./admin/table-management/table-management.component').then(m => m.TableManagementComponent)
      },
      {
        path: 'reservations',
        loadComponent: () => import('./admin/reservation-management/reservation-management.component').then(m => m.ReservationManagementComponent)
      },
      {
        path: 'payments',
        loadComponent: () => import('./admin/payment-management/payment-management.component').then(m => m.PaymentManagementComponent)
      },
      {
        path: 'pending-reservations',
        loadComponent: () => import('./admin/pending-reservations/pending-reservations.component').then(m => m.PendingReservationsComponent)
      }
    ]
  },

  {
    path: '**',
    redirectTo: 'restaurants'
  }
];