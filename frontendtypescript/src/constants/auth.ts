export const tokenTypes = {
  bearer: "Bearer",
  basic: "Basic"
} as const;

export const grantTypes = {
  authorizationCode: "authorization_code",
  clientCredentials: "client_credentials",
  refreshToken: "refresh_token"
} as const;
