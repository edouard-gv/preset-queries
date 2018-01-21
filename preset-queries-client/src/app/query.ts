class Parameter {
  name: string;
  type: string;
  userValue: string;
}

export class Query {
  id: number;
  name: string;
  description: string;
  parameters: Parameter[];
  isEdited: boolean;
  template: string;
  configurationId: number;

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
