import { getOperatorTypeSign, OperatorType } from '@page/policies/model/policy.model';

describe('getOperatorTypeSign', () => {
  it('should return "=" for OperatorType.EQ', () => {
    expect(getOperatorTypeSign(OperatorType.EQ)).toBe('=');
  });

  it('should return "!=" for OperatorType.NEQ', () => {
    expect(getOperatorTypeSign(OperatorType.NEQ)).toBe('!=');
  });

  it('should return "<" for OperatorType.LT', () => {
    expect(getOperatorTypeSign(OperatorType.LT)).toBe('<');
  });

  it('should return ">" for OperatorType.GT', () => {
    expect(getOperatorTypeSign(OperatorType.GT)).toBe('>');
  });

  it('should return "<=" for OperatorType.LTEQ', () => {
    expect(getOperatorTypeSign(OperatorType.LTEQ)).toBe('<=');
  });

  it('should return ">=" for OperatorType.GTEQ', () => {
    expect(getOperatorTypeSign(OperatorType.GTEQ)).toBe('>=');
  });

});
