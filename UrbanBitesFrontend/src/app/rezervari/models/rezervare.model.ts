export interface Rezervare {
  // PK In baza de date 
  rezervare_id: number; 
  
  // Intervalul orar al rezervARII
  interval_orar: string; 
  
  // FK cAtre Masa rezervatA 
  masa_id: number; 
  
  // FK catre utilizatorul care a facut rezervarea
  user_id: number; 
  
  //  FK catre restaurant 
  restaurant_id: number; 
  
  // poate fi util pentru afisare/filtrare
  numar_persoane?: number;
  
  // statutul rezervarii (noua, confirmata, anulata)
  status?: 'noua' | 'confirmata' | 'anulata'; 
}