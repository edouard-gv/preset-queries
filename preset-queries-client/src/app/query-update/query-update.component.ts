import { Component, OnInit, Input } from '@angular/core';
import {Configuration, Parameter, ParameterType, Query} from '../query';
import {QueryService} from '../query.service';
import {isUndefined} from 'util';

@Component({
  selector: 'app-query-update',
  templateUrl: './query-update.component.html',
  styleUrls: ['./query-update.component.css']
})
export class QueryUpdateComponent implements OnInit {
  @Input() query: Query;

  configurations: Configuration[];
  parameterTypes: ParameterType[];

  getMetaData(): void {
    this.queryService.getConfigurations().subscribe(configurations => {
      this.configurations = configurations;
    });
    this.queryService.getParameterTypes().subscribe(parameterTypes => {
      // Decorating with editorial data. Better here than in the back...
      for (const parameter of parameterTypes) {
        switch (parameter.name) {
          case 'FROM':
            parameter.label = 'From clause';
            break;
          case 'FROM_LIST':
            parameter.label = 'From clause (select list)';
            parameter.optionsLabel = 'Values';
            parameter.hint = 'separated by ";". Add a star to the preselected value';
            break;
          case 'WHERE':
            parameter.label = 'Where clause';
            break;
          case 'WHERE_LIST':
            parameter.label = 'Where clause (select list)';
            parameter.optionsLabel = 'Values';
            parameter.hint = 'separated by ";". Add a star to the preselected value';
            break;
          case 'WHERE_OPTIONAL':
            parameter.label = 'Where clause with optional fragment';
            parameter.optionsLabel = 'Optional fragment';
            parameter.hint = 'will be added when parameter is not null';
            break;
          case 'DRILLING_QUERY':
            parameter.label = 'Subquery Link';
            parameter.optionsLabel = 'Name of the subquery';
            parameter.hint = 'all current parameters will be passed to it';
            break;
          default:
            parameter.label = parameter.name;
            parameter.optionsLabel = 'Parameter options';
            parameter.hint = 'to be documented';
        }
      }
      this.parameterTypes = parameterTypes;
    });
  }

  getParameterTypeFromParameterTypeName(parameterTypeName: String): ParameterType {
    if (isUndefined(this.parameterTypes)) {
      console.log('Please wait for parameterTypes to be defined before calling the function getParameterTypeFromParameterTypeName ' +
        'by conditioning your block with *ngIf="parameterTypes"');
    }
    return this.parameterTypes.find(p => p.name === parameterTypeName);
  }
  constructor(
    private queryService: QueryService
  ) { }

  ngOnInit() {
    this.getMetaData();
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
