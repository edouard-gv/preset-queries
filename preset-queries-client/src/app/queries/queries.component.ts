import { Component, OnInit } from '@angular/core';
import {Parameter, Query} from '../query';
import { QueryService} from '../query.service';

@Component({
  selector: 'app-queries',
  templateUrl: './queries.component.html',
  styleUrls: ['./queries.component.css']
})
export class QueriesComponent implements OnInit {

  queries: Query[];

  selectedQuery: Query;

  getQueries(): void {
    this.queryService.getQueries().subscribe(result => this.queries = result);
  }

  onSelect(query: Query): void {
    this.selectedQuery = query;
    for (const parameter of this.selectedQuery.parameters) {
      if (Parameter.isListParameter(parameter)) {
        for (const option of Parameter.getUserValueRawOptionList(parameter)) {
          if (Parameter.isDefaultOption(option)) {
            parameter.userValue = Parameter.cleanOption(option);
          }
        }
      }
    }
  }

  newQuery(): void {
    const aQuery: Query = new Query();
    aQuery.name = 'new name';
    aQuery.isEdited = true;
    this.queries.push(aQuery);
    this.selectedQuery = aQuery;
  }

  constructor(private queryService: QueryService) { }

  ngOnInit() {
    this.getQueries();
  }

  reloadAll(): void {
    this.getQueries();
    this.selectedQuery = null;
  }

}
