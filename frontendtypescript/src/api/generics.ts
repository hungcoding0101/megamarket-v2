import axios, { AxiosError, AxiosRequestConfig } from "axios";
import { toCamelCaseDeeply, toSnakeCaseDeeply } from "../util/generics";
import { ApiResponse } from "../types/dto/api";
import { GetRequestInput, PostRequestInput } from "../types/api";

function prepareRequestConfig(params?: object, headers?: object) {
  const config: AxiosRequestConfig = {};
  if (params != null) {
    config.params = params;
  }
  if (headers != null) {
    config.headers = headers;
  }
  return config;
}

export const makeGetRequest = async (input: GetRequestInput) => {
  try {
    const config = prepareRequestConfig(input.params, input.headers);
    const response = await axios.get(input.url, config);

    let respondeData: ApiResponse = {
      isSuccessful: false,
      error: null,
      value: null
    };

    if (response.data != null) {
      const dataToCamelCase = toCamelCaseDeeply(response.data as object);
      respondeData = dataToCamelCase as ApiResponse;
    }

    if (respondeData.isSuccessful) {
      input.onSuccess?.(respondeData.value as object);
    } else {
      input.onBussinessError?.(respondeData.error as object);
    }
  } catch (error) {
    if (!(error instanceof AxiosError)) {
      throw error;
    }

    console.log("Error", error.message);
  }
};

export const makePostRequest = async (input: PostRequestInput) => {
  try {
    const config = prepareRequestConfig(input.params, input.headers);
    const data = input.data && toSnakeCaseDeeply(input.data);
    const response = await axios.post(input.url, data, config);

    let respondeData: ApiResponse = {
      isSuccessful: false,
      error: null,
      value: null
    };

    if (response.data != null) {
      const dataToCamelCase = toCamelCaseDeeply(response.data as object);
      respondeData = dataToCamelCase as ApiResponse;
    }

    if (respondeData.isSuccessful) {
      input.onSuccess?.(respondeData.value as object);
    } else {
      input.onBussinessError?.(respondeData.error as object);
    }
  } catch (error) {
    if (!(error instanceof AxiosError)) {
      throw error;
    }

    console.log("Error", error.message);
  }
};
