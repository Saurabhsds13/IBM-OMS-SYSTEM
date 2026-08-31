/**
 * Decodes a JWT payload without verifying the signature (client-side display
 * only). Returns null if the token is malformed.
 */
export function decodeJwt(token) {
  if (!token) return null;
  try {
    const payload = token.split('.')[1];
    const json = atob(payload.replace(/-/g, '+').replace(/_/g, '/'));
    return JSON.parse(json);
  } catch {
    return null;
  }
}

/** Reads username + roles from an OMS access token. */
export function userFromToken(token) {
  const claims = decodeJwt(token);
  if (!claims) return null;
  const roles = Array.isArray(claims.roles) ? claims.roles : [];
  const exp = claims.exp ? claims.exp * 1000 : null;
  return { username: claims.sub, roles, exp };
}

export function isExpired(token) {
  const claims = decodeJwt(token);
  if (!claims?.exp) return true;
  return Date.now() >= claims.exp * 1000;
}
