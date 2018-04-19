import { Component, OnInit, Input } from '@angular/core';
import {DataLine, Query, QueryResponse, StructuredQueryResponse} from '../query';
import {QueryService} from '../query.service';

@Component({
  selector: 'app-data-table',
  templateUrl: './data-table.component.html',
  styleUrls: ['./data-table.component.css']
})
export class DataTableComponent implements OnInit {

  @Input() queryResponse: StructuredQueryResponse;

  constructor(public queryService: QueryService) { }

  ngOnInit() { }

  drill(line: DataLine, i: number) {
    line.isDrilled = true;
    // TODO: Compute real Query from injectedQuery and i and data
    this.queryService.getQueries().subscribe(
      (queries: Query[]) =>
        this.queryService.executeQuery(queries[i]).subscribe((serviceQueryResponse: QueryResponse) => {
          if (serviceQueryResponse) {
            line.queryResponse.updateFromServiceQueryResponse(serviceQueryResponse);
          }
        })
      );
  }

}
