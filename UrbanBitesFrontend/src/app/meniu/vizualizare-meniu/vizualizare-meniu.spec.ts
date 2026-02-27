import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VizualizareMeniu } from './vizualizare-meniu';

describe('VizualizareMeniu', () => {
  let component: VizualizareMeniu;
  let fixture: ComponentFixture<VizualizareMeniu>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VizualizareMeniu]
    })
    .compileComponents();

    fixture = TestBed.createComponent(VizualizareMeniu);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
