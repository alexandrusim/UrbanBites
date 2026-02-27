import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';
import { TableService } from '../../core/services/table.service';
import { Table } from '../../shared/models/table.model';

@Component({
  selector: 'app-table-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './table-management.component.html',
  styleUrls: ['./table-management.component.css']
})
export class TableManagementComponent implements OnInit {
  tables: Table[] = [];
  loading = false;
  showModal = false;
  isEditMode = false;
  
  currentTable: Table = this.getEmptyTable();
  restaurantId: number = 4; 

  constructor(
    private tableService: TableService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    const user = this.authService.getCurrentUser();
    
    if (user && (user.role === 'ADMIN_RESTAURANT' || user.role === 'SYSTEM_ADMIN')) {
      if (user.restaurantId) {
        this.restaurantId = user.restaurantId;
      }
      this.loadTables();
    }
  }

  loadTables(): void {
    if (!this.restaurantId) return;
    
    this.loading = true;
    this.tableService.getTablesByRestaurant(this.restaurantId).subscribe({
      next: (data: Table[]) => {
        this.tables = data;
        this.loading = false;
      },
      error: (err: any) => {
        console.error('Eroare la încărcarea meselor:', err);
        this.loading = false;
      }
    });
  }

  openCreateModal(): void {
    this.isEditMode = false;
    this.currentTable = this.getEmptyTable();
    this.showModal = true;
  }

  openEditModal(table: Table): void {
    this.isEditMode = true;
    this.currentTable = { ...table };
    this.showModal = true;
  }

  closeModal(): void {
    this.showModal = false;
  }

  saveTable(): void {
    if (!this.restaurantId) return;
    this.currentTable.restaurantId = this.restaurantId;

    if (this.isEditMode && this.currentTable.tableId) {
      this.tableService.updateTable(this.currentTable.tableId, this.currentTable).subscribe({
        next: () => { this.loadTables(); this.closeModal(); },
        error: (err: any) => console.error('Eroare la actualizare:', err)
      });
    } else {
      this.tableService.createTable(this.currentTable).subscribe({
        next: () => { this.loadTables(); this.closeModal(); },
        error: (err: any) => console.error('Eroare la creare:', err)
      });
    }
  }

  deleteTable(id: number): void {
    if (confirm('Sigur doriți să ștergeți această masă?')) {
      this.tableService.deleteTable(id).subscribe({
        next: () => this.loadTables(),
        error: (err: any) => console.error('Eroare la ștergere:', err)
      });
    }
  }

  toggleAvailability(table: Table): void {
    if (table.tableId) {
      const updated = { ...table, isAvailable: !table.isAvailable };
      this.tableService.updateTable(table.tableId, updated).subscribe({
        next: () => this.loadTables()
      });
    }
  }

  private getEmptyTable(): Table {
    return {
      restaurantId: this.restaurantId,
      tableNumber: '',
      capacity: 2,
      isAvailable: true
    };
  }
}