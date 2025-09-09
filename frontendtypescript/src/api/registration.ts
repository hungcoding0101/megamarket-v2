import {
  EmailOtpSubmissionRequest,
  EmailSubmissionRequest,
  EmailSubmissionResult,
  PasswordSubmissionRequest,
  PasswordSubmissionResult,
  PhoneNumberSubmissionRequest
} from "../types/dto/registration";
import { makePostRequest } from "./generics";
import { PostRequestInput } from "../types/api";

const rootUrl = "user/registration";

export const submitEmail = async (
  request: EmailSubmissionRequest,
  onSuccess: (result: EmailSubmissionResult) => void,
  onBussinessError: (errorCode: string) => void
) => {
  const requestInput: PostRequestInput = {
    url: `${rootUrl}/submit-email`,
    data: request,

    onSuccess: (data: object) => {
      onSuccess(data as EmailSubmissionResult);
    },
    onBussinessError: (error: object) =>
      onBussinessError(error as unknown as string)
  };

  await makePostRequest(requestInput);
};

export const submitEmailOtp = async (
  request: EmailOtpSubmissionRequest,
  onSuccess: () => void,
  onBussinessError: (errorCode: string) => void
) => {
  const requestInput: PostRequestInput = {
    url: `${rootUrl}/verify-email`,
    data: request,

    onSuccess: () => {
      onSuccess();
    },
    onBussinessError: (error: object) =>
      onBussinessError(error as unknown as string)
  };

  await makePostRequest(requestInput);
};

export const submitPhoneNumber = async (
  request: PhoneNumberSubmissionRequest,
  onSuccess: () => void,
  onBussinessError: (errorCode: string) => void
) => {
  const requestInput: PostRequestInput = {
    url: `${rootUrl}/submit-phone-number`,
    data: request,

    onSuccess: () => {
      onSuccess();
    },
    onBussinessError: (error: object) =>
      onBussinessError(error as unknown as string)
  };

  await makePostRequest(requestInput);
};
export const submitPassword = async (
  request: PasswordSubmissionRequest,
  onSuccess: (result: PasswordSubmissionResult) => void,
  onBussinessError: (errorCode: string) => void
) => {
  const requestInput: PostRequestInput = {
    url: `${rootUrl}/submit-password`,
    data: request,

    onSuccess: (data: object) => {
      onSuccess(data as PasswordSubmissionResult);
    },
    onBussinessError: (error: object) =>
      onBussinessError(error as unknown as string)
  };

  await makePostRequest(requestInput);
};
