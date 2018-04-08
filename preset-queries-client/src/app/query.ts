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

  public static isListParameter(parameter: Parameter): boolean {
    return (parameter.type.endsWith("LIST"));
  }

  public static getUserValueRawOptionList(parameter: Parameter): string[] {
    return parameter.optionalFragment.split(";")
  }

  public static isDefaultOption(option: string): boolean {
    return option.endsWith("*");
  }

  public static cleanOption(option: string): string {
    return (Parameter.isDefaultOption(option)
      ? option.slice(0, option.length - 1)
      : option)
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
