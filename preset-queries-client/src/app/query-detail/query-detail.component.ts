import { Component, OnInit, Input } from '@angular/core';
import {Configuration, Parameter, ParameterType, Query} from '../query';
import { QueryService} from '../query.service';

@Component({
  selector: 'app-query-detail',
  templateUrl: './query-detail.component.html',
  styleUrls: ['./query-detail.component.css']
})
export class QueryDetailComponent implements OnInit {

  @Input() query: Query;

  configurations: Configuration[];
  parameterTypes : ParameterType[];

  getMetaData(): void {
    this.queryService.getConfigurations().subscribe(result => this.configurations = result);
    this.queryService.getParameterTypes().subscribe(result =>
    {
      //Decorating with editorial data. Better here than in the back...
      for (var parameter of result) {
        switch (parameter.name) {
          case "FROM":
            parameter.label = "From clause";
            break;
          case "WHERE":
            parameter.label = "Where clause";
            break;
          case "WHERE_OPTIONAL":
            parameter.label = "Where clause with optional fragment";
            parameter.parametersLabel = "Optional fragment";
            parameter.hint = "will be added when parameter is not null";
            break;
          default:
            parameter.label = parameter.name;
        }
      }
      this.parameterTypes = result;
    });
  }

  getParameterTypeFromParameterTypeName(parameterTypeName: String): ParameterType {
    for (var parameter of this.parameterTypes){
      if (parameter.name == parameterTypeName)
        return parameter;
    }
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

  addParameter(): void {
    this.query.parameters.push(new Parameter());
  }

}
