import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-rezervari-root',
  standalone: true,
  imports: [RouterModule],
  template: `
    <div class="rezervari-container">
      <h1>Gestionare Rezervări UrbanBites</h1>
      <router-outlet></router-outlet> 
    </div>
  `
})
export class Rezervari { } 