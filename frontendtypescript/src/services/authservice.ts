import {
  UserManager,
  User,
  UserManagerSettings,
  WebStorageStateStore
} from "oidc-client-ts";

export default class AuthService {
  userManager: UserManager;
  constructor() {
    const settings: UserManagerSettings = {
      authority: import.meta.env.VITE_BASE_API_URL,
      client_id: import.meta.env.VITE_CLIENT_ID,
      client_secret: import.meta.env.VITE_CLIENT_SECRET,
      redirect_uri: import.meta.env.VITE_BASE_FRONT_END_URL,
      post_logout_redirect_uri: `${
        import.meta.env.VITE_BASE_FRONT_END_URL
      }/login`,
      userStore: new WebStorageStateStore({ store: window.localStorage }),
      automaticSilentRenew: true,
      client_authentication: "client_secret_basic"
    };
    this.userManager = new UserManager(settings);
  }

  getUser() {
    return this.userManager.getUser();
  }

  login() {
    return this.userManager.signinRedirect();
  }

  loginCallback() {
    return this.userManager.signinRedirectCallback();
  }

  loginPopup() {
    return this.userManager.signinPopup();
  }

  loginPopupCallback() {
    return this.userManager.signinPopupCallback();
  }

  logout() {
    return this.userManager.signoutRedirect();
  }

  saveUser(user: User) {
    this.userManager.storeUser(user);
  }

  getStoredState(state: string) {
    return this.userManager.settings.stateStore.get(state);
  }
}
