import { CalendarDateModel } from '@core/model/calendar-date.model';
import { Policy } from '@page/policies/model/policy.model';

export class PoliciesAssembler {
  public static assemblePolicy(policy: Policy): Policy {
    console.log(policy.createdOn, new CalendarDateModel(policy.createdOn as string));
    const formattedCreatedOn = new CalendarDateModel(policy.createdOn as string);
    const formattedValidUntil = new CalendarDateModel(policy.validUntil as string);
    return {
      ...policy,
      createdOn: formattedCreatedOn.isInitial() ? null : formattedCreatedOn.valueOf().toISOString().slice(0, 16),
      validUntil: formattedValidUntil.isInitial() ? null : formattedValidUntil.valueOf().toISOString().slice(0, 16),
      accessType: policy.accessType,
    };
  }
}
