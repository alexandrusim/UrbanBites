import { Component, OnInit, ChangeDetectorRef, NgZone } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminService } from '../../core/services/admin.service';
import { UserDTO } from '../../shared/models/admin.model';

@Component({
  selector: 'app-user-management',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './user-management.component.html',
  styleUrls: ['./user-management.component.css']
})
export class UserManagementComponent implements OnInit {
  users: UserDTO[] = [];
  loading: boolean = true;
  error: string = '';

  constructor(
    private adminService: AdminService,
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone
  ) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.loading = true;
    this.error = '';
    
    this.adminService.getAllUsers().subscribe({
      next: (data) => {
        this.ngZone.run(() => {
          this.users = data;
          this.loading = false;
          this.cdr.markForCheck();
        });
      },
      error: (err) => {
        this.ngZone.run(() => {
          this.error = 'Eroare la încărcarea utilizatorilor';
          this.loading = false;
          this.cdr.markForCheck();
        });
      }
    });
  }

  deleteUser(userId: number, userName: string): void {
    if (confirm(`Sigur vrei să ștergi utilizatorul ${userName}?`)) {
      this.adminService.deleteUser(userId).subscribe({
        next: () => {
          this.users = this.users.filter(u => u.userId !== userId);
          alert('Utilizator șters cu succes');
        },
        error: (err) => {
          alert('Eroare la ștergerea utilizatorului');
          console.error(err);
        }
      });
    }
  }

  getRoleBadgeClass(role: string): string {
    return role === 'ADMIN' ? 'badge-admin' : 'badge-client';
  }

  getStatusBadgeClass(isActive: boolean): string {
    return isActive ? 'badge-active' : 'badge-inactive';
  }
}
