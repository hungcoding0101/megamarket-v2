import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { AuthInfo } from "../types/auth";

const initialState: AuthInfo = {
  accessToken: null,
  refreshToken: null,
  isLoggedIn: false
};

const reducers = {
  login: (state: AuthInfo, action: PayloadAction<AuthInfo>) => {
    state.accessToken = action.payload.accessToken;
    state.refreshToken = action.payload.refreshToken;
    state.isLoggedIn = true;
  },
  logout: (state: AuthInfo) => {
    state.accessToken = null;
    state.refreshToken = null;
    state.isLoggedIn = false;
  },
  updateToken: (state: AuthInfo, action: PayloadAction<AuthInfo>) => {
    state.accessToken = action.payload.accessToken;
    state.refreshToken = action.payload.refreshToken;
    state.isLoggedIn = true;
  }
};

export const authSlice = createSlice({
  name: "auth",
  initialState: initialState,
  reducers
});

export const { login, logout, updateToken } = authSlice.actions;
