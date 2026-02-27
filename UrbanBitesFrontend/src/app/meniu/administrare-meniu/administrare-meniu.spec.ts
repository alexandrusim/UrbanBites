import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdministrareMeniu } from './administrare-meniu';

describe('AdministrareMeniu', () => {
  let component: AdministrareMeniu;
  let fixture: ComponentFixture<AdministrareMeniu>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdministrareMeniu]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdministrareMeniu);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
