import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { TwoFactorService } from '../../services/two-factor.service';
import { TwoFaLoginVerify } from '../../models/two-fa.model';

@Component({
  selector: 'app-verify-2fa',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './verify-2fa.component.html',
  styleUrls: ['./verify-2fa.component.css']
})
export class Verify2faComponent implements OnInit {
  verificationCode: string = '';
  email: string = '';
  userId: number = 0;
  returnUrl: string = '/restaurants';

  verifying: boolean = false;
  error: string = '';
  success: boolean = false;

  constructor(
    private twoFactorService: TwoFactorService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const pendingLogin = sessionStorage.getItem('pendingLogin');
    
    if (!pendingLogin) {
      this.router.navigate(['/login']);
      return;
    }

    const loginData = JSON.parse(pendingLogin);
    this.userId = loginData.userId;
    this.email = loginData.email;
    this.returnUrl = loginData.returnUrl || '/restaurants';
  }


  verifyCode(): void {
    if (!this.verificationCode || this.verificationCode.length !== 6) {
      this.error = 'Please enter a valid 6-digit code';
      return;
    }

    this.verifying = true;
    this.error = '';

    const request: TwoFaLoginVerify = {
      code: this.verificationCode
    };

    this.twoFactorService.verify2FaLogin(this.userId, request).subscribe({
      next: (response) => {
        if (response.success) {
          this.success = true;
          
          sessionStorage.removeItem('pendingLogin');
          setTimeout(() => {
            this.router.navigate([this.returnUrl]);
          }, 1500);
        } else {
          this.error = response.message || '2FA verification failed';
        }
        this.verifying = false;
      },
      error: (err) => {
        this.error = err.error?.message || 'Invalid or expired code. Try again.';
        this.verifying = false;
      }
    });
  }


  backToLogin(): void {
    sessionStorage.removeItem('pendingLogin');
    this.router.navigate(['/login']);
  }


  onKeypress(event: KeyboardEvent): void {
    if (event.key === 'Enter' && this.verificationCode.length === 6) {
      this.verifyCode();
    }
  }
}
