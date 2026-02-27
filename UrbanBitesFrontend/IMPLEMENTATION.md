# UrbanBites Frontend - Angular Application

Frontend-ul aplicației UrbanBites pentru sistemul de rezervări la restaurante, dezvoltat cu Angular 21.

## 📋 Funcționalități Implementate

### ✅ Module Complete

1. **Restaurante** (`/restaurants`)
   - Lista restaurantelor active
   - Filtrare după oraș și tip bucătărie
   - Detalii restaurant cu recenzii
   - Navigare către rezervare

2. **Rezervări** (`/rezervari`)
   - Creare rezervare nouă cu selecție restaurant, dată, oră și masă
   - Vizualizare rezervări existente
   - Validare disponibilitate mese
   - Generare cod confirmare

3. **Feedback/Recenzii** (`/feedback`)
   - Formular de recenzie cu rating-uri multiple
   - Rating general și pe categorii (mâncare, serviciu, ambianță, raport calitate-preț)
   - Afișare recenzii pe pagina restaurantului

4. **Navigare**
   - Navbar responsive cu meniu hamburger
   - Routing complet între module

### 📦 Structura Proiectului

```
src/app/
├── core/
│   └── services/          # Servicii pentru comunicare cu backend
│       ├── user.service.ts
│       ├── restaurant.service.ts
│       ├── table.service.ts
│       ├── reservation.service.ts
│       ├── feedback.service.ts
│       └── menu.service.ts
├── shared/
│   ├── models/           # Interfețe TypeScript pentru entități
│   │   ├── user.model.ts
│   │   ├── restaurant.model.ts
│   │   ├── table.model.ts
│   │   ├── reservation.model.ts
│   │   ├── feedback.model.ts
│   │   └── menu.model.ts
│   └── components/
│       └── navbar/       # Bară de navigare
├── restaurants/          # Modul restaurante
│   ├── restaurant-list/
│   └── restaurant-details/
├── rezervari/            # Modul rezervări
│   ├── rezervare-noua/
│   ├── lista-rezervari/
│   └── rezervare-formular/
├── feedback/             # Componente feedback
│   └── feedback-form/
└── meniu/               # Modul meniu (existent)
```

## 🔌 Integrare Backend

Toate serviciile sunt configurate să comunice cu backend-ul Spring Boot pe `http://localhost:8080/api`.

### Endpoints utilizate:

- **Users**: `/api/users`
- **Restaurants**: `/api/restaurants`
- **Tables**: `/api/tables`
- **Reservations**: `/api/reservations`
- **Feedback**: `/api/feedback`
- **Menu Items**: `/api/menu-items`

## 🚀 Instalare și Rulare

### Prerequisite
- Node.js 18+
- npm
- Backend-ul Spring Boot pornit pe localhost:8080

### Instalare
```bash
cd UrbanBitesFrontend
npm install
```

### Development Server
```bash
ng serve
```
Aplicația va fi disponibilă pe `http://localhost:4200`

### Build pentru Producție
```bash
ng build
```

## 📱 Funcționalități pe Module

### Restaurante
- **Lista**: Afișare grid cu filtre după oraș și tip bucătărie
- **Detalii**: Informații complete, contact, facilități, recenzii
- **Acțiuni**: Buton "Rezervă masă" - redirecționează către formular rezervare

### Rezervări
- **Formular nou**: 
  - Selecție restaurant (poate primi ID din query params)
  - Selecție dată și oră din slots predefinite
  - Număr persoane cu validare
  - Selecție automată masă potrivită
  - Durată rezervare (90-180 min)
  - Solicitări speciale
- **Confirmare**: Afișare cod confirmare după creare
- **Lista**: Vizualizare rezervări utilizator (din modul existent)

### Feedback
- **Rating general**: 1-5 stele (obligatoriu)
- **Rating-uri detaliate**: Mâncare, Serviciu, Ambianță, Raport calitate-preț
- **Comentariu**: Text liber (max 1000 caractere)
- **Afișare**: Review-uri vizibile pe pagina restaurantului

## 🎨 Design

- Design responsive pentru mobile, tablet și desktop
- Culori primare: 
  - Primary: #007bff (blue)
  - Success: #28a745 (green)
  - Warning: #f39c12 (orange)
- Componente cu shadow și hover effects
- Animații pentru acțiuni (success icon, form transitions)

## 🔄 Compatibilitate Backend

Serviciile sunt mapate să funcționeze cu:
- Modelul vechi de rezervări (pentru compatibilitate)
- Modelul nou din backend (entități Spring Boot)
- Conversie automată între formate

## 📝 TODO / Funcționalități Viitoare

### Prioritate Mare
- [ ] **Autentificare**: Module users/auth cu login/register
- [ ] **Gestionare sesiune**: Service pentru user curent
- [ ] **Guards**: Protecție rute pentru utilizatori autentificați
- [ ] **Profile**: Pagină profil utilizator cu editare date

### Prioritate Medie
- [ ] **Meniu Items**: Integrare cu backend pentru afișare meniu
- [ ] **Notificări**: Sistem de notificări în-app
- [ ] **Search**: Căutare avansată restaurante
- [ ] **Filtre avansate**: Preț, rating, facilități
- [ ] **Maps**: Integrare Google Maps pentru locații

### Prioritate Scăzută
- [ ] **Plăți**: Integrare sistem de plăți online
- [ ] **Chat**: Suport chat cu restaurantul
- [ ] **Istoric**: Istoric complet rezervări și comenzi
- [ ] **Favorite**: Salvare restaurante favorite
- [ ] **Share**: Partajare pe social media

## 🐛 Debugging

### Erori comune:

1. **CORS Error**: 
   - Verifică că backend-ul rulează
   - Verifică configurarea CORS în `CorsConfig.java`

2. **404 Not Found**:
   - Verifică că endpoint-urile din servicii match cu backend
   - URL-ul backend trebuie să fie `http://localhost:8080`

3. **Module not found**:
   ```bash
   npm install
   ```

## 📚 Resurse

- [Angular Documentation](https://angular.dev)
- [Backend README](../backend/README.md)
- [API Documentation](../backend/README.md#api-endpoints)

## 👥 Contribuție

Pentru a adăuga funcționalități noi:
1. Creează un nou branch
2. Implementează funcționalitatea
3. Testează integrarea cu backend-ul
4. Creează Pull Request

---

**Status**: ✅ Funcțional cu integrare backend completă
**Versiune**: 1.0.0
**Ultima actualizare**: Decembrie 2025
