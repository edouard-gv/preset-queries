import { Component, OnInit } from '@angular/core';
import { Query } from '../query';
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
  }

  newQuery(): void {
    let aQuery: Query = new Query();
    aQuery.name = "new name";
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
