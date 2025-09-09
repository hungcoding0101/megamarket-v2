import { UserRoles } from "../../enums/auth";

export type EmailSubmissionRequest = {
  email: string;
  role: UserRoles;
};

export type EmailOtpSubmissionRequest = {
  registrationToken: string;
  otp: string;
};

export type PhoneNumberSubmissionRequest = {
  registrationToken: string;
  phoneNumber: string;
};
export type PasswordSubmissionRequest = {
  registrationToken: string;
  password: string;
};

export type EmailSubmissionResult = {
  otpCooldownSeconds: number;
  registrationToken: string;
};
export type PasswordSubmissionResult = {
  accessToken: string;
};
