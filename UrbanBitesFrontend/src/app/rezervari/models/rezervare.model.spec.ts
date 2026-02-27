import { Rezervare } from './rezervare.model';

describe('Rezervare Model', () => {
  it('should create an object conforming to the Rezervare interface', () => {
    const mockRezervare: Rezervare = {
      rezervare_id: 99,
      interval_orar: '2025-12-05 18:00',
      masa_id: 5,
      user_id: 101,
      restaurant_id: 1,
      numar_persoane: 4,
      status: 'noua'
    };
    
    expect(mockRezervare).toBeTruthy();
    expect(mockRezervare.masa_id).toEqual(5);
  });
});