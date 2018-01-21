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
  source: string;
}

export class QueryResponse {
  query: Query;
  message: string;
  header: string[];
  data: string[][];
  jdbcTemplate: string;
}

