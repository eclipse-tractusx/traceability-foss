import { ConstraintLogicType, OperatorType } from '@page/policies/model/policy.model';

export const OperatorTypesAsSelectOptionsList: any[] = Object.keys(OperatorType).map(key => {
  let convertedOperatorType = '';
  switch (key) {
    case OperatorType.EQ:
      convertedOperatorType = '=';
      break;
    case OperatorType.NEQ:
      convertedOperatorType = '!=';
      break;
    case OperatorType.LT:
      convertedOperatorType = '<';
      break;
    case OperatorType.GT:
      convertedOperatorType = '>';
      break;
    case OperatorType.LTEQ:
      convertedOperatorType = '<=';
      break;
    case OperatorType.GTEQ:
      convertedOperatorType = '>=';
      break;
    default:
      convertedOperatorType = key;
  }
  return {
    label: convertedOperatorType, value: convertedOperatorType,
  };
});

export const ConstraintLogicTypeAsSelectOptionsList: any[] = Object.keys(ConstraintLogicType).map(key => {
  console.log(key);
  return {
    label: key, value: key,
  };
});
