import { Component, OnInit, Input } from '@angular/core';
import {Parameter, Query, QueryResponse} from '../query';
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

  public getExecutionParameters(): Parameter[] {
    return this.query.parameters.filter(p => p.type !== 'DRILLING_QUERY');
  }

  public hasExecutionParameters(): boolean {
    return this.query.parameters.filter(p => p.type !== 'DRILLING_QUERY').length > 0;
  }

  isListParameter(parameter: Parameter): boolean {
    return Parameter.isListParameter(parameter);
  }

  getCleanUserValueOptionList(parameter: Parameter): string[] {
    return Parameter.getUserValueRawOptionList(parameter).map(Parameter.cleanOption);
  }

  execute(): void {
    this.queryService.executeMainQuery(this.query);
  }

  edit(): void {
    this.query.isEdited = true;
  }
}
