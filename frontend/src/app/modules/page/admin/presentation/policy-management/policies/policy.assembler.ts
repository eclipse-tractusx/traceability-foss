import { CalendarDateModel } from '@core/model/calendar-date.model';
import {
  getOperatorTypeSign,
  OperatorType,
  Policy,
  PolicyAction,
  PolicyEntry,
  PolicyResponseMap,
} from '@page/policies/model/policy.model';

export class PoliciesAssembler {
  public static assemblePolicy(policy: Policy): Policy {
    const formattedCreatedOn = new CalendarDateModel(policy.createdOn as string);
    const formattedValidUntil = new CalendarDateModel(policy.validUntil as string);
    return {
      ...policy,
      policyName: policy.policyId,
      createdOn: formattedCreatedOn.isInitial() ? null : formattedCreatedOn.valueOf().toISOString().slice(0, 16),
      validUntil: formattedValidUntil.isInitial() ? null : formattedValidUntil.valueOf().toISOString().slice(0, 16),
      accessType: policy.permissions[0].action.toUpperCase() as PolicyAction,
      constraints: policy.constraints ?? this.mapDisplayPropsToPolicyRootLevelFromPolicy(policy),
    };
  }

  public static mapToPolicyEntryList(policyResponse: PolicyResponseMap): PolicyEntry[] {
    const list: PolicyEntry[] = [];
    for (const [ key, value ] of Object.entries(policyResponse)) {
      value.forEach((entry) => {
        entry.payload.policy.bpn = key;
        entry.payload.policy.constraints = this.mapDisplayPropsToPolicyRootLevelFromPolicyEntry(entry);
        list.push(entry);
      });
    }
    return list;
  }

  public static mapDisplayPropsToPolicyRootLevelFromPolicyEntry(entry: PolicyEntry): string[] {
    entry.payload.policy.policyName = entry.payload['@id'];
    entry.payload.policy.accessType = entry.payload.policy.permissions[0].action;
    let constrainsList = [];
    entry.payload.policy.permissions.forEach(permission => {
      permission.constraint?.and?.forEach((andConstraint, index) => {
        constrainsList.push(andConstraint.leftOperand);
        constrainsList.push(getOperatorTypeSign(OperatorType[andConstraint.operator['@id'].toUpperCase()]));
        constrainsList.push(andConstraint['odrl:rightOperand']);
        if (index !== permission.constraint.and.length - 1) {
          constrainsList.push(' AND ');
        }
      });
      permission.constraint?.or?.forEach((orConstraint, index) => {
        constrainsList.push(orConstraint.leftOperand);
        constrainsList.push(getOperatorTypeSign(OperatorType[orConstraint.operator['@id'].toUpperCase()]));
        constrainsList.push(orConstraint['odrl:rightOperand']);
        if (index !== permission.constraint.or.length - 1) {
          constrainsList.push(' OR ');
        }
      });
    });
    return constrainsList;
  }

  public static mapDisplayPropsToPolicyRootLevelFromPolicy(policy: Policy): string[] {

    let constrainsList = [];
    policy.permissions.forEach(permission => {
      permission.constraints?.and?.forEach(andConstraint => {
        constrainsList.push(andConstraint.leftOperand);
        constrainsList.push(getOperatorTypeSign(andConstraint.operatorTypeResponse));
        constrainsList.push(andConstraint.rightOperand);
        constrainsList.push(' AND ');
      });
      permission.constraints?.or?.forEach(orConstraint => {
        constrainsList.push(orConstraint.leftOperand);
        constrainsList.push(getOperatorTypeSign(orConstraint.operatorTypeResponse));
        constrainsList.push(orConstraint.rightOperand);
        constrainsList.push(' OR ');
      });
    });
    return constrainsList;
  }

  /*
  public static validatePoliciesTemplate(data: any) {

    if (typeof data !== 'object' || data === null || !Array.isArray(data[Object.keys(data)[0]])) {
      return false;
    }

    for (const entry of data[Object.keys(data)[0]]) {
      if (typeof entry.validUntil !== 'string') {
        return false;
      }

      const payload = entry.payload;
      if (typeof payload !== 'object' || payload === null) {
        return false;
      }

      const context = payload['@context'];
      if (typeof context !== 'object' || context === null) {
        return false;
      }

      if (typeof payload['@id'] !== 'string') {
        return false;
      }

      const policy = payload.policy;
      if (typeof policy !== 'object' || policy === null) {
        return false;
      }

      if (typeof policy.policyId !== 'string' ||
        typeof policy.createdOn !== 'string' ||
        typeof policy.validUntil !== 'string' ||
        !Array.isArray(policy.permissions)) {
        return false;
      }

      for (const permission of policy.permissions) {
        if (typeof permission.action !== 'string') {
          return false;
        }

        const constraint = permission.constraint;
        if (typeof constraint !== 'object' || constraint === null || !Array.isArray(constraint.and) || (constraint.or !== null && !Array.isArray(constraint.or))) {
          return false;
        }

        if (constraint.and !== null) {
          for (const andConstraint of constraint.and) {
            if (typeof andConstraint.leftOperand !== 'string' ||
              typeof andConstraint.operator !== 'object' ||
              andConstraint.operator === null ||
              typeof andConstraint.operator['@id'] !== 'string' ||
              typeof andConstraint['odrl:rightOperand'] !== 'string') {
              return false;
            }
          }
        }

        if (constraint.or !== null) {
          for (const orConstraint of constraint.or) {
            if (typeof orConstraint.leftOperand !== 'string' ||
              typeof orConstraint.operator !== 'object' ||
              orConstraint.operator === null ||
              typeof orConstraint.operator['@id'] !== 'string' ||
              typeof orConstraint['odrl:rightOperand'] !== 'string') {
              return false;
            }
          }
        }
      }
    }

    return true;
  }

   */

}
