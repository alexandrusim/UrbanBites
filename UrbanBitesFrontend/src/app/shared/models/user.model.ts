export interface User {
  userId?: number;
  firstName: string;
  lastName: string;
  email: string;
  passwordHash?: string;
  phoneNumber?: string;
  role: 'CLIENT' | 'ADMIN_RESTAURANT' | 'SYSTEM_ADMIN';
  restaurantId?: number;
  isActive?: boolean;
  emailVerified?: boolean;
  createdAt?: string;
  updatedAt?: string;
  lastLoginAt?: string;
}

export interface UserCreateRequest {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  phoneNumber?: string;
  role?: string;
}

export interface UserUpdateRequest {
  firstName?: string;
  lastName?: string;
  phoneNumber?: string;
  isActive?: boolean;
}
