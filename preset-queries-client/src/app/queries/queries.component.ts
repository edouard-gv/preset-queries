import { Component, OnInit } from '@angular/core';
import {Parameter, Query} from '../query';
import { QueryService} from '../query.service';
import {isUndefined} from 'util';

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

  deleteQuery(query: Query): void {
    if (confirm('Are you sure to delete ' + query.name + '?')) {
      if (!this.selectedQuery.id) {
        this.queries.pop();
        this.selectedQuery = null;
      } else {
        this.queryService.deleteQuery(query).subscribe(() => this.reloadAll());
      }
    }
  }

  noNewQueryOnGoing(): boolean {
    return (this.queries.length > 0 && !isUndefined(this.queries[this.queries.length - 1].id));
  }

  constructor(public queryService: QueryService) { }

  ngOnInit() {
    this.getQueries();
  }

  reloadAll(): void {
    this.getQueries();
    this.selectedQuery = null;
  }

}
