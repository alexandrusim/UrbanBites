import { Component, OnInit, ChangeDetectorRef, NgZone } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NotificationService } from '../../core/services/notification.service';
import { AuthService } from '../../core/services/auth.service';
import { Notification } from '../../shared/models/notification.model';

@Component({
  selector: 'app-notification-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './notification-list.component.html',
  styleUrls: ['./notification-list.component.css']
})
export class NotificationListComponent implements OnInit {
  notifications: Notification[] = [];
  isLoading = true;
  userId: number | null = null;
  errorMessage: string = '';

  constructor(
    private notificationService: NotificationService,
    private authService: AuthService,
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone
  ) {}

  ngOnInit(): void {
    const currentUser: any = this.authService.getCurrentUser();
    
    if (currentUser) {
        this.userId = currentUser.userId || currentUser.id || null;
    }

    console.log('Current User ID:', this.userId);

    if (this.userId) {
      this.loadNotifications();
    } else {
      this.errorMessage = 'Utilizatorul nu este autentificat corect.';
      this.isLoading = false;
    }
  }

  loadNotifications(): void {
    if (!this.userId) return;
    this.isLoading = true;

    this.notificationService.getUserNotifications(this.userId).subscribe({
      next: (data) => {
        console.log('📥 Notificări primite de la server:', data);

        this.ngZone.run(() => {
          if (!data) {
            this.notifications = [];
          } else {
            this.notifications = data.sort((a, b) => 
              new Date(b.sentAt).getTime() - new Date(a.sentAt).getTime()
            );
          }

          this.isLoading = false;
          this.updateUnreadCount();
          this.cdr.markForCheck();
        });
      },
      error: (err) => {
        this.ngZone.run(() => {
          console.error('❌ Eroare la încărcarea notificărilor:', err);
          this.errorMessage = 'Nu s-au putut încărca notificările.';
          this.isLoading = false;
          this.cdr.markForCheck();
        });
      }
    });
  }

  markAsRead(notification: Notification): void {
    if (notification.isRead) return;

    this.notificationService.markAsRead(notification.notificationId).subscribe(() => {
      notification.isRead = true;
      this.updateUnreadCount();
    });
  }

  markAllRead(): void {
    if (!this.userId) return;
    this.notificationService.markAllAsRead(this.userId).subscribe(() => {
      this.notifications.forEach(n => n.isRead = true);
      this.updateUnreadCount();
    });
  }

  deleteNotification(id: number, event: Event): void {
    event.stopPropagation();
    if(!confirm('Ștergi această notificare?')) return;

    this.notificationService.deleteNotification(id).subscribe(() => {
      this.notifications = this.notifications.filter(n => n.notificationId !== id);
      this.updateUnreadCount();
    });
  }

  private updateUnreadCount(): void {
    if(this.userId) this.notificationService.updateUnreadCount(this.userId);
  }

  deleteAll(): void {
    if (!this.userId) return;
    
    if (!confirm('Ești sigur că vrei să ștergi TOATE notificările?')) {
      return;
    }

    this.notificationService.deleteUserNotifications(this.userId).subscribe({
      next: () => {
        this.notifications = []; 
        this.updateUnreadCount();
      },
      error: (err:any) => console.error('Eroare la ștergerea tuturor notificărilor', err)
    });
  }
}