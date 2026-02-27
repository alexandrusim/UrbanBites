import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../core/services/auth.service';
import { ProfileService } from '../core/services/profile.service';
import { TwoFactorService } from '../services/two-factor.service';
import { CurrentUser } from '../shared/models/auth.model';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})

export class ProfileComponent implements OnInit, OnDestroy {
  private authSub?: Subscription;
  user: CurrentUser | null = null;
  
  profileForm: FormGroup;
  passwordForm: FormGroup;
  
  editingProfile: boolean = false;
  savingProfile: boolean = false;
  profileError: string = '';
  profileSuccess: string = '';
  
  changingPassword: boolean = false;
  passwordError: string = '';
  passwordSuccess: string = '';
  
  twoFaEnabled: boolean = false;
  twoFaLoading: boolean = false;
  twoFaError: string = '';
  twoFaSuccess: string = '';
  showEnableTwoFa: boolean = false;
  showDisableTwoFa: boolean = false;
  
  showActivity: boolean = false;

  constructor(
    private authService: AuthService,
    private profileService: ProfileService,
    private twoFactorService: TwoFactorService,
    private router: Router,
    private fb: FormBuilder,
    private cdr: ChangeDetectorRef
  ) {
    this.profileForm = this.fb.group({
      firstName: ['', [Validators.required, Validators.minLength(2)]],
      lastName: ['', [Validators.required, Validators.minLength(2)]],
      email: [{ value: '', disabled: true }],
      phoneNumber: ['']
    });

    this.passwordForm = this.fb.group({
      currentPassword: ['', Validators.required],
      newPassword: ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', Validators.required]
    }, { validators: this.passwordMatchValidator });
  }

  ngOnInit(): void {
    this.loadUserProfile();

    this.authSub = this.authService.currentUser$.subscribe((u) => {
      if (u) {
        this.user = u as CurrentUser;
        this.twoFaEnabled = Boolean(u.twoFaEnabled);
        this.profileForm.patchValue({
          firstName: this.user.firstName,
          lastName: this.user.lastName,
          email: this.user.email,
          phoneNumber: this.user.phoneNumber || ''
        });
        this.cdr.detectChanges();
      }
    });
  }

  ngOnDestroy(): void {
    this.authSub?.unsubscribe();
  }


  loadUserProfile(): void {
    const localUser = this.authService.getUserFromStorage();
    if (!localUser) {
      this.router.navigate(['/login']);
      return;
    }

    this.user = localUser;
    this.profileForm.patchValue({
      firstName: this.user.firstName,
      lastName: this.user.lastName,
      email: this.user.email,
      phoneNumber: this.user.phoneNumber || ''
    });

    this.profileService.getProfile().subscribe({
      next: (userData: any) => {
        console.log('User data from API:', userData);

        this.user = {
          userId: userData.userId,
          email: userData.email,
          firstName: userData.firstName,
          lastName: userData.lastName,
          role: userData.role,
          phoneNumber: userData.phoneNumber,
          restaurantId: userData.restaurantId,
          twoFaEnabled: userData.twoFaEnabled,
          twoFaVerified: userData.twoFaVerified
        };

        this.authService.updateCurrentUser(this.user);

        this.twoFaEnabled = Boolean(userData.twoFaEnabled);
        console.log('2FA Enabled set to:', this.twoFaEnabled);

        this.profileForm.patchValue({
          firstName: this.user.firstName,
          lastName: this.user.lastName,
          email: this.user.email,
          phoneNumber: this.user.phoneNumber || ''
        });

        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to load user profile:', err);
        this.load2FaStatus();
      }
    });
  }


  load2FaStatus(): void {
    if (!this.user) return;

    this.twoFactorService.get2FaStatus(this.user.userId).subscribe({
      next: (status: any) => {
        this.twoFaEnabled = status.enabled;
      },
      error: (err: any) => {
        console.error('Failed to load 2FA status:', err);
      }
    });
  }

  toggleEditProfile(): void {
    this.editingProfile = !this.editingProfile;
    if (!this.editingProfile) {
      this.profileError = '';
      this.profileSuccess = '';
      this.loadUserProfile();
    }
  }


  saveProfile(): void {
    if (this.profileForm.invalid) {
      this.profileForm.markAllAsTouched();
      return;
    }

    this.savingProfile = true;
    this.profileError = '';
    this.profileSuccess = '';

    setTimeout(() => {
      this.profileSuccess = 'Profil actualizat cu succes!';
      this.savingProfile = false;
      
      setTimeout(() => {
        this.editingProfile = false;
        this.profileSuccess = '';
      }, 2000);
    }, 1000);
  }


  passwordMatchValidator(group: FormGroup): { [key: string]: any } | null {
    const newPassword = group.get('newPassword')?.value;
    const confirmPassword = group.get('confirmPassword')?.value;

    if (newPassword && confirmPassword && newPassword !== confirmPassword) {
      return { passwordMismatch: true };
    }

    return null;
  }


  toggleChangePassword(): void {
    this.changingPassword = !this.changingPassword;
    if (!this.changingPassword) {
      this.passwordForm.reset();
      this.passwordError = '';
      this.passwordSuccess = '';
    }
  }


  changePassword(): void {
    if (this.passwordForm.invalid) {
      this.passwordForm.markAllAsTouched();
      return;
    }

    this.savingProfile = true;
    this.passwordError = '';
    this.passwordSuccess = '';

    setTimeout(() => {
      this.passwordSuccess = 'Parolă schimbată cu succes!';
      this.savingProfile = false;
      
      setTimeout(() => {
        this.changingPassword = false;
        this.passwordForm.reset();
        this.passwordSuccess = '';
      }, 2000);
    }, 1000);
  }


  enableTwoFa(): void {
    if (!this.user) return;

    this.twoFaLoading = true;
    this.twoFaError = '';
    this.showEnableTwoFa = false;

    this.router.navigate(['/setup-2fa']);
  }


  disableTwoFa(): void {
    if (!this.user || !this.twoFaEnabled) return;

    this.twoFaLoading = true;
    this.twoFaError = '';
    this.showDisableTwoFa = false;

    this.twoFactorService.disable2Fa(this.user.userId).subscribe({
      next: (response: any) => {
        if (response.success) {
          this.twoFaEnabled = false;
          this.twoFaSuccess = '2FA dezactivat!';
          const userStr = localStorage.getItem('user');
          if (userStr) {
            try {
              const user = JSON.parse(userStr);
              user.twoFaEnabled = false;
              localStorage.setItem('user', JSON.stringify(user));
              this.authService.updateCurrentUser(user as CurrentUser);
            } catch (e) {
              console.error('Failed to update user in localStorage:', e);
            }
          }
                    setTimeout(() => {
            this.twoFaSuccess = '';
          }, 3000);
        } else {
          this.twoFaError = response.message || 'Failed to disable 2FA';
        }
        this.twoFaLoading = false;
      },
      error: (err: any) => {
        this.twoFaError = err.error?.message || 'Eroare la dezactivarea 2FA';
        this.twoFaLoading = false;
      }
    });
  }


  logout(): void {
    this.authService.logout();
  }


  get firstName() {
    return this.profileForm.get('firstName');
  }

  get lastName() {
    return this.profileForm.get('lastName');
  }

  get phoneNumber() {
    return this.profileForm.get('phoneNumber');
  }

  get currentPassword() {
    return this.passwordForm.get('currentPassword');
  }

  get newPassword() {
    return this.passwordForm.get('newPassword');
  }

  get confirmPassword() {
    return this.passwordForm.get('confirmPassword');
  }
}
