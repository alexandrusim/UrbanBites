import { Component, OnInit, Inject, Renderer2 } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule, DOCUMENT } from '@angular/common'; 
import { AuthService } from './core/services/auth.service';
import { NavbarComponent } from './shared/components/navbar/navbar.component';
import { NotificationService } from './core/services/notification.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterModule, NavbarComponent],
  templateUrl: './app.html',
  styleUrls: ['./app.css']
})
export class AppComponent implements OnInit {
  title = 'UrbanBites';
  unreadCount = 0;

  constructor(
    public authService: AuthService,
    public notificationService: NotificationService,
    private router: Router,
    @Inject(DOCUMENT) private document: Document, 
    private renderer: Renderer2 
  ) {}

  ngOnInit(): void {

    this.detectExtensionContext();


    this.notificationService.unreadCount$.subscribe(count => this.unreadCount = count);

    if (this.authService.isLoggedIn) {
      const user = this.authService.getCurrentUser();
      const userId = user ? ((user as any).userId || (user as any).id) : null;
      if (userId) this.notificationService.updateUnreadCount(userId);
    }
  }

  private detectExtensionContext(): void {

    const isChromeExtension = typeof window !== 'undefined' && window.location.protocol === 'chrome-extension:';
    
    if (isChromeExtension) {

      this.renderer.addClass(this.document.body, 'is-extension');
    } else {

      this.renderer.removeClass(this.document.body, 'is-extension');
    }
  }

  onLogout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}