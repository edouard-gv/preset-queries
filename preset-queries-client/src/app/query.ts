export class ParameterType {
  name: string;
  parametersLabel: string;
  hint: string;
  label: string;
}

export class Parameter {
  name: string;
  type: ParameterType;
  optionalFragment: string;
  userValue: string;

  constructor() {
    this.type=new ParameterType();
    this.type.name = 'WHERE';
  }
}

//Light objects are copy of objects but enum class replaced by string
//Ugly but did not find other way to map with enum in Java
//Should migrate enum parametertype to plain object
class LightParameter {
  name: string;
  type: string;
  optionalFragment: string;
  userValue: string;

  constructor(hp: Parameter) {
    this.name = hp.name;
    this.type = hp.type.name;
    this.optionalFragment = hp.optionalFragment;
    this.userValue = hp.userValue;
  }
}

export class LightQuery {
  id: number;
  name: string;
  description: string;
  parameters: LightParameter[];
  isEdited: boolean;
  template: string;
  configurationId: number;

  constructor(hq: Query) {
    this.id = hq.id;
    this.name = hq.name;
    this.description = hq.description;
    this.parameters = [];
    this.isEdited = hq.isEdited;
    this.template = hq.template;
    this.configurationId = hq.configurationId;

    for (var hp of hq.parameters) {
      this.parameters.push(new LightParameter(hp));
    }
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
