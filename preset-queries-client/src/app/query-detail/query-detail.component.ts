import { Component, OnInit, Input } from '@angular/core';
import {Configuration, ParameterType, Query} from '../query';
import { QueryService} from '../query.service';

@Component({
  selector: 'app-query-detail',
  templateUrl: './query-detail.component.html',
  styleUrls: ['./query-detail.component.css']
})
export class QueryDetailComponent implements OnInit {

  customCompareParameterType(o1: ParameterType, o2: ParameterType): boolean {
    return (o1 == null && o2 == null) || (o1 != null && o2 != null) && (o1.name == o2.name);
  }

  @Input() query: Query;

  configurations: Configuration[];
  parameterTypes : ParameterType[];

  getMetaData(): void {
    this.queryService.getConfigurations().subscribe(result => this.configurations = result);
    this.queryService.getParameterTypes().subscribe(result => this.parameterTypes = result);
  }

  constructor(
    private queryService: QueryService
  ) { }

  ngOnInit() {
    this.getMetaData()
  }

  execute(): void {
    this.queryService.executeQuery(this.query);
  }

  update(): void {
    this.queryService.updateQuery(this.query);
  }

  reload(): void {
    this.queryService.reloadQuery(this.query);
  }

  edit(): void {
    this.queryService.editQuery(this.query);
  }

}
