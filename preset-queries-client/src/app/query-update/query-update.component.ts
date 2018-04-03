import { Component, OnInit, Input } from '@angular/core';
import {Configuration, Parameter, ParameterType, Query} from "../query";
import {QueryService} from "../query.service";

@Component({
  selector: 'app-query-update',
  templateUrl: './query-update.component.html',
  styleUrls: ['./query-update.component.css']
})
export class QueryUpdateComponent implements OnInit {
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
            parameter.parametersLabel = "Parameter parameters";
            parameter.hint = "to be documented";
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

  addParameter(): void {
    this.query.parameters.push(new Parameter());
  }

  update(): void {
    this.queryService.updateQuery(this.query);
  }

  reload(): void {
    this.queryService.reloadQuery(this.query);
  }

}
