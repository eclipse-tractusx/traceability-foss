import { CalendarDateModel } from '@core/model/calendar-date.model';
import {
  getOperatorTypeSign,
  OperatorType,
  Policy,
  PolicyAction,
  PolicyEntry,
  PolicyResponseMap,
} from '@page/policies/model/policy.model';
import { isNumber } from 'lodash-es';

export class PoliciesAssembler {
  public static assemblePolicy(policy: Policy): Policy {
    const formattedCreatedOn = new CalendarDateModel(policy.createdOn as string);
    const formattedValidUntil = new CalendarDateModel(policy.validUntil as string);
    return {
      ...policy,
      policyName: policy.policyId,
      createdOn: isNumber(policy.createdOn) ? new Date(policy.createdOn as number * 1000).toISOString().slice(0, 19) + 'Z' : (formattedCreatedOn.isInitial() ? null : formattedCreatedOn.valueOf().toISOString().slice(0, 16)),
      validUntil: isNumber(policy.validUntil) ? new Date(policy.validUntil as number * 1000).toISOString().slice(0, 19) + 'Z' :  (formattedValidUntil.isInitial() ? null : this.mapUtcToLocalTime(policy.validUntil)),
      accessType: policy.permissions[0].action.toUpperCase() as PolicyAction,
      constraints: policy.constraints ?? this.mapDisplayPropsToPolicyRootLevelFromPolicy(policy),
    };
  }

  public static mapUtcToLocalTime(validUntil: string): string {
    const date = new Date(validUntil);

    const localYear = date.getFullYear();
    const localMonth = String(date.getMonth() + 1).padStart(2, '0');
    const localDate = String(date.getDate()).padStart(2, '0');
    const localHours = String(date.getHours()).padStart(2, '0');
    const localMinutes = String(date.getMinutes()).padStart(2, '0');
    const localSeconds = String(date.getSeconds()).padStart(2, '0');

  return `${ localYear }-${ localMonth }-${ localDate }T${ localHours }:${ localMinutes }:${ localSeconds }`;

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

  public static mapDisplayPropsToPolicyRootLevelFromPolicyEntry(entry: PolicyEntry): string {
    entry.payload.policy.policyName = entry.payload['@id'];
    entry.payload.policy.accessType = entry.payload.policy.permissions[0].action;
    let constrainsList = [];
    entry.payload.policy.permissions.forEach(permission => {
      permission.constraint?.and?.forEach((andConstraint, index) => {
        constrainsList.push(andConstraint.leftOperand);
        constrainsList.push(getOperatorTypeSign(OperatorType[andConstraint.operator['@id'].toUpperCase()]));
        constrainsList.push(andConstraint.rightOperand);
        if (index !== permission.constraint.and.length - 1) {
          constrainsList.push('*AND ');
        }
      });
      permission.constraint?.or?.forEach((orConstraint, index) => {
        constrainsList.push(orConstraint.leftOperand);
        constrainsList.push(getOperatorTypeSign(OperatorType[orConstraint.operator['@id'].toUpperCase()]));
        constrainsList.push(orConstraint.rightOperand);
        if (index !== permission.constraint.or.length - 1) {
          constrainsList.push('*OR ');
        }
      });
    });
    return this.formatConstraintsListForTable(constrainsList);
  }

  public static mapDisplayPropsToPolicyRootLevelFromPolicy(policy: Policy): string {
    let constrainsList = [];
    policy.permissions.forEach((permission) => {
      permission.constraints?.and?.forEach((andConstraint, index) => {
        constrainsList.push(andConstraint.leftOperand);
        constrainsList.push(getOperatorTypeSign(andConstraint.operatorTypeResponse));
        constrainsList.push(andConstraint.rightOperand);
        if (index !== permission.constraints.and.length - 1) {
          constrainsList.push('*AND ');
        }
      });
      permission.constraints?.or?.forEach((orConstraint, index) => {
        constrainsList.push(orConstraint.leftOperand);
        constrainsList.push(getOperatorTypeSign(orConstraint.operatorTypeResponse));
        constrainsList.push(orConstraint.rightOperand);
        if (index !== permission.constraints.or.length - 1) {
          constrainsList.push('*OR ');
        }
      });
    });
    return this.formatConstraintsListForTable(constrainsList);
  }

  public static formatConstraintsListForTable(constrainsList: string[]): string {
    let formattedString = constrainsList.toString().replaceAll(',', '');
    formattedString = formattedString.replaceAll('*', '<br>');
    formattedString = this.removeAfterThird('<br>', formattedString);
    return formattedString;
  }

  public static removeAfterThird(substring: string, text: string): string {
    let index = 0;
    let count = 0;

    // Continuously update the index to the next occurrence of the substring
    while ((index = text.indexOf(substring, index)) !== -1) {
      count++;
      if (count === 3) {
        // Return the substring up to and including the third occurrence
        return text.substring(0, index + substring.length);
      }
      // Advance index past the current match
      index += substring.length;
    }

    // Return the original text if less than three occurrences are found
    return text;
  }

  /**
   * This Feature is commented out for now, because uploading/downloading Templates/Policies is
   * currently not a requirement but could be one in future.
   */
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
