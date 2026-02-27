import { Component, OnInit, ChangeDetectorRef, NgZone } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AdminService } from '../../core/services/admin.service';
import { AuthService } from '../../core/services/auth.service';
import { DashboardStats, ActivityLog } from '../../shared/models/admin.model';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css']
})
export class AdminDashboardComponent implements OnInit {
  stats: DashboardStats | null = null;
  loading: boolean = true;
  error: string = '';
  activityLogs: ActivityLog[] = [];
  logsLoading: boolean = false;
  logsError: string = '';
  showLogsPanel: boolean = false;
  pasteLogs: string = '';

  constructor(
    private adminService: AdminService,
    public authService: AuthService,
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone
  ) {}

  ngOnInit(): void {
    this.loadStats();
    if (this.isSystemAdmin) {
      this.loadActivityLogs();
    }
  }

  loadStats(): void {
    this.loading = true;
    this.adminService.getDashboardStats().subscribe({
      next: (data) => {
        this.ngZone.run(() => {
          this.stats = data;
          this.loading = false;
          this.cdr.markForCheck();
        });
      },
      error: (err) => {
        this.ngZone.run(() => {
          this.error = 'Eroare la încărcarea statisticilor';
          this.loading = false;
          this.cdr.markForCheck();
        });
        console.error(err);
      }
    });
  }

  loadActivityLogs(): void {
    this.logsLoading = true;
    if (!this.isSystemAdmin) {
      this.logsError = 'Doar SYSTEM_ADMIN poate prelua activity logs de pe server.';
      this.logsLoading = false;
      this.cdr.markForCheck();
      return;
    }

    this.adminService.getActivityLogs().subscribe({
      next: (data) => {
        this.ngZone.run(() => {
          this.activityLogs = data.slice(0, 50);
          this.logsLoading = false;
          this.cdr.markForCheck();
        });
      },
      error: (err) => {
        this.ngZone.run(() => {
          this.logsError = 'Eroare la încărcarea activity logs';
          this.logsLoading = false;
          this.cdr.markForCheck();
        });
        console.error(err);
      }
    });
  }

  toggleLogsPanel(): void {
    this.showLogsPanel = !this.showLogsPanel;
    if (this.showLogsPanel && this.activityLogs.length === 0) {
      this.loadActivityLogs();
    }
  }

  loadFromPaste(): void {
    if (!this.pasteLogs) return;
    try {
      const parsed = JSON.parse(this.pasteLogs);
      if (Array.isArray(parsed)) {
        this.activityLogs = parsed.slice(0, 200);
      } else {
        this.activityLogs = [parsed] as any;
      }
      this.logsError = '';
    } catch (e) {
      this.logsError = 'JSON invalid pentru activity logs';
    }
    this.cdr.markForCheck();
  }

  get isSystemAdmin(): boolean {
    return this.authService.isSystemAdmin();
  }

  get isRestaurantAdmin(): boolean {
    return this.authService.isRestaurantAdmin();
  }
}
