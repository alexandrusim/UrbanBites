import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RezervareFormular } from './rezervare-formular';

describe('RezervareFormular', () => {
  let component: RezervareFormular;
  let fixture: ComponentFixture<RezervareFormular>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RezervareFormular]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RezervareFormular);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
