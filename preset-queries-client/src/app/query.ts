class Parameter {
  name: string;
  type: string;
  userValue: string;
}

export class Query {
  name: string;
  description: string;
  parameters: Parameter[];
}

export class QueryResponse {
  query: Query;
  message: string;
  header: string[];
  data: string[][];
}

