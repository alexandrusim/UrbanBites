export interface TwoFaSetup {
  secret: string;
  qrCode: string;
  email: string;
}

export interface TwoFaVerify {
  secret: string;
  code: string;
}

export interface TwoFaLoginVerify {
  code: string;
}

export interface TwoFaResponse {
  success: boolean;
  message: string;
}

export interface TwoFaStatus {
  twoFaEnabled: boolean;
  twoFaVerified: boolean;
}
