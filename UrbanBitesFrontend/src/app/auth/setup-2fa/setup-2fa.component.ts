import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { TwoFactorService } from '../../services/two-factor.service';
import { TwoFaVerify } from '../../models/two-fa.model';

@Component({
  selector: 'app-setup-2fa',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './setup-2fa.component.html',
  styleUrls: ['./setup-2fa.component.css']
})
export class Setup2faComponent implements OnInit {
  qrCode: string | null = null;
  secret: string | null = null;
  verificationCode: string = '';
  email: string = '';
  
  loading: boolean = false;
  verifying: boolean = false;
  error: string = '';
  success: string = '';
  step: 'setup' | 'verify' = 'setup';

  userId: number = 0;

  constructor(
    private twoFactorService: TwoFactorService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    const user = localStorage.getItem('user');
    if (user) {
      try {
        const userData = JSON.parse(user);
        this.userId = userData.userId;
        this.email = userData.email;
      } catch (e) {
        console.error('Failed to parse user from localStorage:', e);
        this.router.navigate(['/login']);
        return;
      }
    }
    
    if (this.userId === 0) {
      this.router.navigate(['/login']);
      return;
    }

    this.initiate2FaSetup();
  }


  initiate2FaSetup(): void {
    this.loading = true;
    this.error = '';

    console.log('Initiating 2FA setup for userId:', this.userId);

    this.twoFactorService.setupTwoFa(this.userId).subscribe({
      next: (response) => {
        console.log('2FA Setup Response:', response);
        console.log('QR Code received:', response.qrCode ? 'YES' : 'NO');
        console.log('QR Code length:', response.qrCode?.length);
        console.log('Secret received:', response.secret ? 'YES' : 'NO');
        
        this.qrCode = response.qrCode;
        this.secret = response.secret;
        this.step = 'setup';
        this.loading = false;
        
        this.cdr.detectChanges();
        
        console.log('Loading state after update:', this.loading);
        console.log('Step after update:', this.step);
      },
      error: (err) => {
        console.error('2FA Setup Error:', err);
        console.error('Error status:', err.status);
        console.error('Error message:', err.message);
        
        if (err.status === 401) {
          this.error = 'Authentication failed. Please login again.';
        } else if (err.status === 403) {
          this.error = 'Access denied. Please check your permissions.';
        } else {
          this.error = err.error?.message || 'Failed to setup 2FA. Please try again.';
        }
        
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }


  verifyCode(): void {
    if (!this.verificationCode || this.verificationCode.length !== 6) {
      this.error = 'Please enter a 6-digit code';
      return;
    }

    if (!this.secret) {
      this.error = 'Secret not found. Please restart setup.';
      return;
    }

    this.verifying = true;
    this.error = '';

    const request: TwoFaVerify = {
      secret: this.secret,
      code: this.verificationCode
    };

    this.twoFactorService.verifyAndEnable2Fa(this.userId, request).subscribe({
      next: (response) => {
        if (response.success) {
          this.success = '2FA enabled successfully!';
          this.step = 'verify';
          
          const userStr = localStorage.getItem('user');
          if (userStr) {
            try {
              const user = JSON.parse(userStr);
              user.twoFaEnabled = true;
              localStorage.setItem('user', JSON.stringify(user));
            } catch (e) {
              console.error('Failed to update user in localStorage:', e);
            }
          }
          
          setTimeout(() => {
            this.router.navigate(['/profile']);
          }, 2000);
        } else {
          this.error = response.message || 'Verification failed';
        }
        this.verifying = false;
      },
      error: (err) => {
        this.error = err.error?.message || 'Invalid code or expired. Please try again.';
        this.verifying = false;
      }
    });
  }

  restartSetup(): void {
    this.verificationCode = '';
    this.error = '';
    this.success = '';
    this.initiate2FaSetup();
  }


  copyToClipboard(text: string | null): void {
    if (!text) return;
    
    navigator.clipboard.writeText(text).then(() => {
      this.success = 'Copiat in clipboard!';
      setTimeout(() => {
        this.success = '';
      }, 2000);
    }).catch(err => {
      console.error('Failed to copy:', err);
      this.error = 'Eroare la copiere';
    });
  }
}
