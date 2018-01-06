class Parameter {
  label: string;
  type: string;
  userValue: string;
}

export class Query {
  id: number;
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

