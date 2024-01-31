// TODO: Decide if long term a Policy state, facade, ResponseType and Assembler is needed
export interface Policy {
  policyId: string;
  createdOn: string;
  validUntil: string;
  permissions?: PolicyPermission[];
}

export interface PolicyPermission {
  action: PolicyType;
  constraints?: Constraint[];
}

export enum PolicyType {
  ACCESS="ACCESS",
  USE="USE"
}

export interface Constraint {
  leftOperand: string;
  operator: OperatorType;
  rightOperand: string[];
}

export enum OperatorType {
    EQ = 'eq',
    NEQ = 'neq',
    LT = 'lt',
    GT = 'gt',
    IN = 'in',
    LTEQ = 'lteq',
    GTEQ = 'gteq',
    ISA = 'isA',
    HASPART = 'hasPart',
    ISPARTOF = 'isPartOf',
    ISONEOF = 'isOneOf',
    ISALLOF = 'isAllOf',
    ISNONEOF = 'isNoneOf',
}
