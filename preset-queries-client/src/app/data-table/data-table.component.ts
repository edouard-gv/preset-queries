import { Component, OnInit, Input } from '@angular/core';
import {DataLine, Parameter, Query, QueryResponse, StructuredQueryResponse} from '../query';
import {QueryService} from '../query.service';
import {isUndefined} from "util";

@Component({
  selector: 'app-data-table',
  templateUrl: './data-table.component.html',
  styleUrls: ['./data-table.component.css']
})
export class DataTableComponent implements OnInit {

  @Input() queryResponse: StructuredQueryResponse;

  constructor(public queryService: QueryService) { }

  drill(line: DataLine, position: number) {
    line.isDrilled = true;
    this.queryService.getQueries().subscribe(
      (queries: Query[]) => {
        let targetQuery = queries.find(q=>q.name.toUpperCase()===this.getDrillingQueryName(position).toUpperCase());
        let originalQuery = this.queryResponse.query;
        //First we copy into the target query all parameters from current query which name matches
        DataTableComponent.filterExecutionParameters(targetQuery)
          .map(p=> {
            let originalParameter = DataTableComponent.findExecutionParameterByName(originalQuery, p.name);
            if (!isUndefined(originalParameter)) {
              p.userValue = originalParameter.userValue;
            }
          });

        //Then we add the current line cell value as a new parameters if the target query has a parameter with the column name
        for (let i=0; i<this.queryResponse.header.length; i++) {
          let targetParameter = DataTableComponent.findExecutionParameterByName(targetQuery, this.queryResponse.header[i]);
          if (!isUndefined(targetParameter)) {
            console.log("Setting '"+targetParameter.name+"' parameter value to '"+line.cellArray[i]+"'");
            targetParameter.userValue = line.cellArray[i];
          }
        }
        this.queryService.executeQuery(targetQuery).subscribe((serviceQueryResponse: QueryResponse) => {
          if (serviceQueryResponse) {
            line.queryResponse.updateFromServiceQueryResponse(serviceQueryResponse);
          }
        });
      }
  );
  }

  ngOnInit() { }

  private static findDrillingParameterByName(query: Query, name: string): Parameter {
    return query.parameters
      .filter(p => (p.type === 'DRILLING_QUERY'))
      .find(p => p.name.toUpperCase() === name.toUpperCase())
  }

  private static filterExecutionParameters(query: Query): Parameter[] {
    return query.parameters
      .filter(p => (p.type !== 'DRILLING_QUERY'));
  }

  private static findExecutionParameterByName(query: Query, name: string): Parameter {
    return query.parameters
      .filter(p => (p.type !== 'DRILLING_QUERY'))
      .find(p => p.name.toUpperCase() === name.toUpperCase())
  }

  private getDrillingQueryName(position: number): string {
    if (!this.isDrilling(position)) {
      throw 'Should never happen: looking for a drilling query on a non drilling parameter';
    }
    return DataTableComponent.findDrillingParameterByName(this.queryResponse.query, this.queryResponse.header[position]).optionalFragment;
  }

  public isDrilling(i: number): boolean {
    return !isUndefined(DataTableComponent.findDrillingParameterByName(this.queryResponse.query, this.queryResponse.header[i]));
  }
}
