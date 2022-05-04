export interface Token {
  preferred_username?: string;
  given_name?: string;
  family_name?: string;
  email?: string;
  mspid?: string;
  realm_access: {
    roles: [];
  };
  auth_time?: string;
}
