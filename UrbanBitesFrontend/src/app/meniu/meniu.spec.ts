import { TestBed } from '@angular/core/testing';

import { Meniu } from './meniu';

describe('Meniu', () => {
  let service: Meniu;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Meniu);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
