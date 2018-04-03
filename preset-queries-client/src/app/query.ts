export class ParameterType {
  name: string;
  isParameterized: boolean;
  parametersLabel: string;
  hint: string;
  label: string;
}

export class Parameter {
  name: string;
  type: string;
  optionalFragment: string;
  userValue: string;

  constructor() {
    this.type = 'WHERE';
  }
}

export class Query {
  id: number;
  name: string;
  description: string;
  parameters: Parameter[];
  isEdited: boolean;
  template: string;
  configurationId: number;

  constructor() {
    this.parameters = [];
  }

  public merge(result: Query, query: Query): void {
    query.name = result.name;
    query.description = result.description;
    query.parameters = result.parameters;
    query.isEdited = result.isEdited;
    query.template = result.template;
    query.configurationId = result.configurationId;
  }
}

export class QueryResponse {
  query: Query;
  message: string;
  header: string[];
  data: string[][];
  jdbcTemplate: string;
}

export class Configuration {
  id: number;
  name: string;
}
