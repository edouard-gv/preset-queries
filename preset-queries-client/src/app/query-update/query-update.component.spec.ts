import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { QueryUpdateComponent } from './query-update.component';

describe('QueryUpdateComponent', () => {
  let component: QueryUpdateComponent;
  let fixture: ComponentFixture<QueryUpdateComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ QueryUpdateComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(QueryUpdateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
