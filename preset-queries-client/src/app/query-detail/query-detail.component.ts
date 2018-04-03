import { Component, OnInit, Input } from '@angular/core';
import {Parameter, Query} from '../query';
import { QueryService} from '../query.service';

@Component({
  selector: 'app-query-detail',
  templateUrl: './query-detail.component.html',
  styleUrls: ['./query-detail.component.css']
})
export class QueryDetailComponent implements OnInit {

  @Input() query: Query;

  constructor(
    private queryService: QueryService
  ) { }

  ngOnInit() {

  }

  execute(): void {
    this.queryService.executeQuery(this.query);
  }

  edit(): void {
    this.queryService.editQuery(this.query);
  }
}
