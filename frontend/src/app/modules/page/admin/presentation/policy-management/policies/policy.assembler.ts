import { CalendarDateModel } from '@core/model/calendar-date.model';
import { Policy, PolicyAction, PolicyEntry, PolicyResponseMap } from '@page/policies/model/policy.model';

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
      constraints: this.mapDisplayPropsToPolicyRootLevelFromPolicy(policy),
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
      permission.constraint.and.forEach(andConstraint => {
        constrainsList.push(andConstraint.leftOperand);
        constrainsList.push(andConstraint.operator['@id']);
        constrainsList.push(andConstraint['odrl:rightOperand']);
      });
      permission.constraint?.or?.forEach(orConstraint => {
        constrainsList.push(orConstraint.leftOperand);
        constrainsList.push(orConstraint.operator['@id']);
        constrainsList.push(orConstraint['odrl:rightOperand']);
      });
    });
    return constrainsList;
  }

  public static mapDisplayPropsToPolicyRootLevelFromPolicy(policy: Policy): string[] {

    let constrainsList = [];
    policy.permissions.forEach(permission => {
      permission.constraints?.and?.forEach(andConstraint => {
        constrainsList.push(andConstraint.leftOperand);
        constrainsList.push(andConstraint.operatorTypeResponse);
        constrainsList.push(andConstraint.rightOperand);
      });
      permission.constraints?.or?.forEach(orConstraint => {
        constrainsList.push(orConstraint.leftOperand);
        constrainsList.push(orConstraint.operatorTypeResponse);
        constrainsList.push(orConstraint.rightOperand);
      });
    });
    return constrainsList;
  }

}
