import { Component, OnInit, ChangeDetectorRef, NgZone } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { MeniuService } from '../../core/services/menu.service';
import { AuthService } from '../../core/services/auth.service';
import { UserService } from '../../core/services/user.service';

interface MenuItem {
  id?: number;
  menuItemId?: number;
  itemId?: number;
  name: string;
  description: string;
  price: number;
  category: string;
  imageUrl?: string;
  available: boolean;
  restaurantId?: number;
  preparationTime?: number;
  allergens?: string;
}

@Component({
  selector: 'app-menu-management',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule],
  templateUrl: './menu-management.component.html',
  styleUrls: ['./menu-management.component.css']
})
export class MenuManagementComponent implements OnInit {
  menuItems: MenuItem[] = [];
  filteredItems: MenuItem[] = [];
  categories: any[] = [];
  menus: any[] = []; // Pentru gestionarea multiplelor liste de meniu ale unui restaurant
  
  loading: boolean = false;
  selectedCategory: any = ''; 
  isEditing = false;
  showForm = false;
  showMenuForm = false; // Pentru modalul de creare meniu nou
  newMenuName = '';
  newMenuDesc = '';
  
  menuForm: FormGroup;
  currentId: number | null = null;
  restaurantId: number | null = null;
  selectedMenuId: number | null = null;

  constructor(
    private menuService: MeniuService,
    private authService: AuthService,
    private userService: UserService,
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone
  ) {
    this.menuForm = this.fb.group({
      name: ['', Validators.required],
      description: ['', Validators.required],
      price: [0, [Validators.required, Validators.min(0)]],
      menuId: [''],
      category: ['', Validators.required],
      imageUrl: [''],
      available: [true],
      preparationTime: [''],
      allergens: ['']
    });
  }

  ngOnInit(): void {
    // 1. Prioritate: ID-ul din calea URL (pentru Sys Admin care vine din lista de restaurante)
    const pathId = this.route.snapshot.paramMap.get('id');
    if (pathId) {
      this.restaurantId = +pathId;
      this.initData();
      return;
    }

    // 2. Fallback: ID-ul din Query Params
    const queryId = this.route.snapshot.queryParams['restaurantId'];
    if (queryId) {
      this.restaurantId = +queryId;
      this.initData();
      return;
    }

    // 3. Fallback: ID-ul din profilul utilizatorului (pentru Admin de Restaurant)
    const user = this.authService.getCurrentUser();
    if (user && user.restaurantId) {
      this.restaurantId = user.restaurantId;
      this.initData();
    } else if (user && user.userId) {
      this.userService.getUserById(user.userId).subscribe({
        next: (fullUser: any) => {
          if (fullUser.restaurantId) {
            this.restaurantId = fullUser.restaurantId;
            this.initData();
          }
        }
      });
    }
  }

  initData(): void {
    this.loadMenus(); // Aceasta va apela ulterior loadMenuItems și loadCategories
  }

  loadMenus(): void {
    if (!this.restaurantId) return;
    this.menuService.getMenusByRestaurant(this.restaurantId).subscribe({
      next: (data: any[]) => {
        this.menus = data || [];
        if (this.menus.length > 0) {
          this.selectedMenuId = this.menus[0].menuId || this.menus[0].id || null;
        }
        this.loadMenuItems();
      },
      error: () => this.loadMenuItems()
    });
  }

  loadMenuItems(): void {
    if (!this.restaurantId) return;

    this.loading = true;
    this.menuService.getAllProductsForRestaurant(this.restaurantId).subscribe({
      next: (data) => {
        this.ngZone.run(() => {
          const enriched = (data || []).map((item: any) => ({
            ...item,
            available: item.available !== undefined ? item.available : true
          }));

          // Filtrăm produsele în funcție de meniul selectat (dacă există)
          if (this.selectedMenuId) {
            this.menuItems = enriched.filter((it: any) => (it.menuId || it.menuID) == this.selectedMenuId);
          } else {
            this.menuItems = enriched;
          }

          this.extractCategories(enriched);
          this.filterByCategory();
          this.loading = false;
          this.cdr.detectChanges();
        });
      },
      error: (err) => {
        console.error('Eroare loadMenuItems:', err);
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  // Soluția pentru lipsa API-ului de categorii: le extragem din produse
  extractCategories(items: any[]): void {
    const unique = new Set<string>();
    items.forEach(it => {
      const cat = it.category || it.categorie;
      if (cat) unique.add(cat);
    });
    
    if (unique.size > 0) {
      this.categories = Array.from(unique).map(name => ({ name }));
    } else {
      // Fallback dacă nu există încă produse cu categorii
      this.categories = [
        { name: 'Fel Principal' }, { name: 'Supe' }, { name: 'Desert' }, 
        { name: 'Bauturi' }, { name: 'Pizza' }
      ];
    }
  }

  filterByCategory(): void {
    if (!this.selectedCategory || this.selectedCategory === '') {
      this.filteredItems = [...this.menuItems];
    } else {
      const categoryName = this.selectedCategory.name || this.selectedCategory;
      this.filteredItems = this.menuItems.filter(item => item.category === categoryName);
    }
  }

  // --- Gestionare Formular Produse ---

  onSubmit(): void {
    if (this.menuForm.invalid || !this.restaurantId) return;

    const formValue = this.menuForm.value;
    const productData = {
      ...formValue,
      restaurantId: this.restaurantId,
      menuId: this.selectedMenuId || 2 // Fallback pe meniul 2 dacă nu e definit
    };

    const request = (this.isEditing && this.currentId != null) 
      ? this.menuService.updatePreparat(this.currentId, productData)
      : this.menuService.createPreparat(productData);

    request.subscribe({
      next: () => {
        this.loadMenuItems();
        this.closeForm();
      }
    });
  }

  // --- Alte Metode (Păstrate din varianta ta funcțională) ---

  toggleAvailability(item: MenuItem): void {
    const newStatus = !item.available;
    const id = item.itemId || item.menuItemId || item.id;
    if (!id) return;
    item.available = newStatus;
    this.menuService.updatePreparat(id, item).subscribe({ error: () => item.available = !newStatus });
  }

  openAddForm(): void {
    this.isEditing = false;
    this.currentId = null;
    this.menuForm.reset({ available: true, price: 0 });
    this.showForm = true;
  }

  openEditForm(item: MenuItem): void {
    this.isEditing = true;
    this.currentId = item.itemId || item.menuItemId || item.id || null; 
    this.menuForm.patchValue({ ...item });
    this.showForm = true;
  }

  closeForm(): void { this.showForm = false; this.menuForm.reset(); }

  deleteItem(item: any): void {
    const id = item.itemId || item.menuItemId || item.id;
    if (!id || !confirm(`Sigur vrei să ștergi "${item.name}"?`)) return;
    this.menuService.stergePreparat(id).subscribe({
      next: () => this.loadMenuItems()
    });
  }

  // Metode pentru Meniuri (Logica nouă a colegului)
  openCreateMenu(): void { this.showMenuForm = true; }
  cancelCreateMenu(): void { this.showMenuForm = false; }
  createMenu(): void {
    if (!this.restaurantId || !this.newMenuName) return;
    const payload = { name: this.newMenuName, description: this.newMenuDesc, restaurantId: this.restaurantId, active: true };
    this.menuService.createMenu(payload).subscribe({
      next: () => { this.showMenuForm = false; this.loadMenus(); }
    });
  }
}