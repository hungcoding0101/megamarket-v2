export type GetRequestInput = {
  url: string;
  params?: object;
  headers?: object;
  onSuccess?: (data: object) => void;
  onBussinessError?: (errorObject: object) => void;
};

export type PostRequestInput = {
  url: string;
  data?: object;
  params?: object;
  headers?: object;
  onSuccess?: (data: object) => void;
  onBussinessError?: (errorObject: object) => void;
};
