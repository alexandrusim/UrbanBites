import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule, ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';
import { isPlatformBrowser } from '@angular/common';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, RouterModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  loading: boolean = false;
  error: string = '';
  returnUrl: string = '/restaurants';

  private authService = inject(AuthService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private fb = inject(FormBuilder);

  constructor() {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    if (this.authService.isLoggedIn) {
      this.router.navigate(['/restaurants']);
    }

    const navigation = this.router.getCurrentNavigation();
    if (navigation?.extras?.state && isPlatformBrowser(navigation.extras.state['platformId'])) {
      const storedReturnUrl = sessionStorage.getItem('returnUrl');
      if (storedReturnUrl) {
        this.returnUrl = storedReturnUrl;
        sessionStorage.removeItem('returnUrl'); // Clear it
      }
    }
  }

  onSubmit() {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.error = '';

    this.authService.login(this.loginForm.value).subscribe({
      next: (response) => {
        this.loading = false;
        
        localStorage.setItem('token', response.token);
        
        const userData = {
          userId: response.userId,
          email: response.email || this.loginForm.value.email,
          role: response.role,
          twoFaEnabled: response.twoFaEnabled
        };
        localStorage.setItem('user', JSON.stringify(userData));
        
        if (response.twoFaEnabled) {
          sessionStorage.setItem('pendingLogin', JSON.stringify({
            userId: response.userId,
            email: response.email || this.loginForm.value.email,
            token: response.token,
            returnUrl: this.returnUrl
          }));
          
          this.router.navigate(['/verify-2fa']);
        } else {
          this.router.navigate([this.returnUrl]);
        }
      },
      error: (err) => {
        this.loading = false;
        this.error = err.error?.message || 'Autentificare eșuată. Verifică datele introduse.';
      }
    });
  }

  get email() {
    return this.loginForm.get('email');
  }

  get password() {
    return this.loginForm.get('password');
  }
}