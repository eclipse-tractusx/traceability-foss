import { OperatorType } from '@page/policies/model/policy.model';

export const OperatorTypesAsSelectModel: any[] = Object.keys(OperatorType).map(key => {
  console.log(key);
  return {
    label: key, value: key,
  };
});
