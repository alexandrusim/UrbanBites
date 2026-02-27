import { TestBed } from '@angular/core/testing';

import { Rezervari } from './rezervari';

describe('Rezervari', () => {
  let service: Rezervari;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Rezervari);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
