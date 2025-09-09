export type User = {
  id: string;
  email: string;
  phoneNumber: string;
  role: string;
  firstName: string;
  lastName: string;
  avatar: string;
};

export type AuthInfo = {
  accessToken: string | null;
  refreshToken: string | null;
  isLoggedIn: boolean;
};
