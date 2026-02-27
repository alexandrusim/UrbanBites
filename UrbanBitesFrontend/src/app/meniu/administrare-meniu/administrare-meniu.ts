import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, FormArray, Validators, ReactiveFormsModule, FormControl } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';

// --- IMPORTURILE CORECTE (Din Core și Shared) ---
import { MeniuService } from '../../core/services/menu.service'; 
import { AuthService } from '../../core/services/auth.service';
// Dacă ai nevoie de tipuri, le iei de aici:
// import { MenuItem } from '../../shared/models/menu.model';

@Component({
  standalone: true,
  selector: 'app-administrare-meniu',
  templateUrl: './administrare-meniu.component.html', 
  styleUrls: ['./administrare-meniu.component.css'],
  imports: [ReactiveFormsModule, CommonModule] 
})
export class AdministrareMeniuComponent implements OnInit {
  preparatForm!: FormGroup;
  titluPagina: string = 'Adaugă Preparat Nou';
  esteEditare: boolean = false;
  preparatId: number | null = null;
  restaurantId: number | null = null; 
  
  constructor(
    private fb: FormBuilder,
    private meniuService: MeniuService, // Serviciul central din Core
    private authService: AuthService, 
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.construiesteFormular();
  }
  
  ngOnInit(): void {
    // 1. Identificăm Restaurantul
    const queryParams = this.route.snapshot.queryParams;
    if (queryParams['restaurantId']) {
      this.restaurantId = +queryParams['restaurantId'];
    }

    if (!this.restaurantId) {
      const user = this.authService.getCurrentUser();
      if (user && user.restaurantId) {
        this.restaurantId = user.restaurantId;
      }
    }

    // 2. Verificăm dacă suntem pe Editare
    const idParam = this.route.snapshot.params['id'];
    this.preparatId = idParam ? +idParam : null;
    this.esteEditare = !!this.preparatId;

    if (this.esteEditare && this.preparatId) {
      this.titluPagina = 'Editează Preparatul';
      this.incarcaPreparatPentruEditare(this.preparatId);
    }
  }
  
  construiesteFormular(): void {
    // Păstrăm numele câmpurilor în română pentru a se potrivi cu HTML-ul tău
    this.preparatForm = this.fb.group({
      nume: ['', [Validators.required, Validators.minLength(3)]],
      descriere: ['', Validators.required],
      pret: [0.01, [Validators.required, Validators.min(0.01)]],
      categorie: ['', Validators.required],
      imagine_url: [''], 
      alergeni: this.fb.array([])
    });
  }

  createAlergenFormGroup(alergen: string): FormControl {
    return this.fb.control(alergen, Validators.required);
  }

  get alergeni(): FormArray {
    return this.preparatForm.get('alergeni') as FormArray;
  }

  adaugaAlergen(): void {
    this.alergeni.push(this.fb.control('', Validators.required));
  }

  eliminaAlergen(i: number): void {
    this.alergeni.removeAt(i);
  }

  incarcaPreparatPentruEditare(id: number): void {
    this.meniuService.getMenuItemById(id).subscribe({
      next: (preparat: any) => {
        this.preparatForm.patchValue({
            nume: preparat.name,
            descriere: preparat.description,
            pret: preparat.price,
            categorie: preparat.category,
            imagine_url: preparat.imageUrl
        });
        
        const alergeniArray = this.preparatForm.get('alergeni') as FormArray;
        alergeniArray.clear();
        
        if (preparat.allergens) {
           const lista = Array.isArray(preparat.allergens) ? preparat.allergens : preparat.allergens.split(',');
           lista.forEach((alergen: string) => {
             if(alergen.trim()) alergeniArray.push(this.createAlergenFormGroup(alergen.trim()));
           });
        }
      },
      error: (err) => console.error('Eroare la încărcarea produsului:', err)
    });
  }

  onSubmit(): void {
    if (this.preparatForm.valid) {
      const formValue = this.preparatForm.value;

      const preparatData = {
        name: formValue.nume,
        description: formValue.descriere,
        price: formValue.pret,
        category: formValue.categorie,
        imageUrl: formValue.imagine_url,
        allergens: formValue.alergeni.join(','), 
        restaurantId: this.restaurantId,
        menuId: 1 
      };

      if (this.esteEditare && this.preparatId) {
        this.meniuService.updateMenuItem(this.preparatId, preparatData).subscribe({
          next: () => this.navigheazaLaLista(),
          error: (err) => console.error('Eroare update:', err)
        });
      } else {
        this.meniuService.createMenuItem(preparatData).subscribe({
          next: () => this.navigheazaLaLista(),
          error: (err) => console.error('Eroare creare:', err)
        });
      }
    } else {
      this.preparatForm.markAllAsTouched();
    }
  }

  navigheazaLaLista(): void {
    this.router.navigate(['/admin/menu'], { queryParamsHandling: 'preserve' });
  }
}