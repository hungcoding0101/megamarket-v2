export class ApiResponse {
  isSuccessful: boolean;
  value: unknown;
  error: unknown;

  constructor(isSuccessful: boolean, value: unknown, error: unknown) {
    this.isSuccessful = isSuccessful;
    this.value = value;
    this.error = error;
  }
}
