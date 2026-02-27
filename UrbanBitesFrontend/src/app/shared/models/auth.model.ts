export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  phoneNumber?: string;
}

export interface AuthResponse {
  token: string;
  email: string;
  role: string;
  userId: number;
  firstName: string;
  lastName: string;
  restaurantId?: number;
  twoFaEnabled?: boolean;
  twoFaVerified?: boolean;
}

export interface CurrentUser {
  userId: number;
  email: string;
  firstName: string;
  lastName: string;
  role: string;
  phoneNumber?: string;
  restaurantId?: number;
  twoFaEnabled?: boolean;
  twoFaVerified?: boolean;
}
