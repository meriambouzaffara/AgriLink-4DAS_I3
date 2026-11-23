export enum UserRole {
  CLIENT = 'CLIENT',
  ADMIN = 'ADMIN',
  SERVICE_CLIENT = 'SERVICE_CLIENT',
  AGRICULTEUR = 'AGRICULTEUR',
}

export function normalizeUserRole(
  value: string | undefined | null,
): UserRole | null {
  if (!value) {
    return null;
  }
  const upper = value.trim().toUpperCase();
  const validRoles = Object.values(UserRole) as string[];
  if (validRoles.includes(upper)) {
    return upper as UserRole;
  }
  return null;
}

export function isUserRole(value: string): value is UserRole {
  return normalizeUserRole(value) !== null;
}
