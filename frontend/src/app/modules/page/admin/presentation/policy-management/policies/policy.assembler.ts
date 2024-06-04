import { CalendarDateModel } from '@core/model/calendar-date.model';
import { Policy, PolicyAction, PolicyEntry, PolicyResponseMap } from '@page/policies/model/policy.model';

export class PoliciesAssembler {
  public static assemblePolicy(policy: Policy): Policy {
    const formattedCreatedOn = new CalendarDateModel(policy.createdOn as string);
    const formattedValidUntil = new CalendarDateModel(policy.validUntil as string);
    return {
      ...policy,
      createdOn: formattedCreatedOn.isInitial() ? null : formattedCreatedOn.valueOf().toISOString().slice(0, 16),
      validUntil: formattedValidUntil.isInitial() ? null : formattedValidUntil.valueOf().toISOString().slice(0, 16),
      accessType: policy.accessType.toUpperCase() as PolicyAction,
    };
  }

  public static mapToPolicyEntryList(policyResponse: PolicyResponseMap): PolicyEntry[] {
    const list: PolicyEntry[] = [];
    for (const [ key, value ] of Object.entries(policyResponse)) {
      value.forEach((entry) => {
        entry.payload.policy.bpn = key;
        entry.payload.policy.constraints = this.mapDisplayPropsToPolicyRootLevel(entry);
        list.push(entry);
      });
    }
    return list;
  }

  public static mapDisplayPropsToPolicyRootLevel(entry: PolicyEntry): string[] {
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

}
