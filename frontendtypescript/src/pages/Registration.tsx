import { useCallback, useState } from "react";
import EmailSubmission from "../components/EmailSubmission";
import { Button, Col, Modal, Row, Spin, Steps } from "antd";
import PhoneNumberSubmission from "../components/PhoneNumberSubmission";
import OtpSubmission from "../components/OtpSubmission";
import { OtpFunctionStatus } from "../enums/otp";
import PasswordSubmission from "../components/PasswordSubmission";
import {
  submitEmail,
  submitEmailOtp,
  submitPassword,
  submitPhoneNumber
} from "../api/registration";
import { UserRoles } from "../enums/auth";
import {
  EmailSubmissionResult,
  PasswordSubmissionRequest,
  PasswordSubmissionResult,
  PhoneNumberSubmissionRequest
} from "../types/dto/registration";
import { useTranslation } from "react-i18next";

function Registration() {
  const i18Prefix = "registration";

  const [currentStep, setCurrentStep] = useState(0);
  const [farthestStep, setFarthestStep] = useState(0);
  const [otpStatus, setOtpStatus] = useState(OtpFunctionStatus.NORMAL);
  const [registrationToken, setRegistrationToken] = useState("");
  const [isProcessing, setIsProcessing] = useState(false);
  const { t: translation } = useTranslation();

  const [otpCanBeResentAt, setOtpCanBeResentAt] = useState<Date>();

  const next = () => {
    setFarthestStep(Math.max(farthestStep, currentStep + 1));
    setCurrentStep(currentStep + 1);
  };
  const prev = () => {
    setCurrentStep(currentStep - 1);
  };

  const onEmailSubmissionSuccess = (result: EmailSubmissionResult) => {
    const timeToResend = Date.now() + result.otpCooldownSeconds * 1000;
    setRegistrationToken(result.registrationToken);
    setOtpCanBeResentAt(new Date(timeToResend));
    next();
  };

  const onOtpResend = useCallback(() => {
    setOtpStatus(OtpFunctionStatus.RESENDING);
    setOtpCanBeResentAt(new Date(Date.now() + 3 * 1000));
    setOtpStatus(OtpFunctionStatus.NORMAL);
  }, []);

  const onOtpSubmission = useCallback(
    async (value: string) => {
      setOtpStatus(OtpFunctionStatus.VERIFYING);
      setIsProcessing(true);

      await submitEmailOtp(
        { otp: value, registrationToken },
        onOtpSubmissionSuccess,
        handleBusinessErroṛ
      );

      setIsProcessing(false);
      setOtpStatus(OtpFunctionStatus.NORMAL);
    },
    [farthestStep, currentStep]
  );

  const onOtpSubmissionSuccess = () => {
    next();
  };

  const onPhoneNumberSubmission = async (phoneNumber: string) => {
    setIsProcessing(true);

    const request: PhoneNumberSubmissionRequest = {
      registrationToken,
      phoneNumber
    };

    await submitPhoneNumber(
      request,
      onPhoneNumberSubmissionSuccess,
      handleBusinessErroṛ
    );

    setIsProcessing(false);
  };

  const onPhoneNumberSubmissionSuccess = () => {
    next();
  };

  const onPasswordSubmission = async (password: string) => {
    setIsProcessing(true);

    const request: PasswordSubmissionRequest = {
      registrationToken,
      password
    };

    await submitPassword(
      request,
      onPasswordSubmissionSuccess,
      handleBusinessErroṛ
    );

    setIsProcessing(false);
  };

  const onPasswordSubmissionSuccess = (result: PasswordSubmissionResult) => {
    Modal.success({ title: result.accessToken });
  };

  const handleBusinessErroṛ = (errorCode: string) => {
    Modal.error({
      title: translation(`${i18Prefix}.errors.${errorCode}`)
    });
  };

  const steps = [
    {
      title: "Submit email",
      content: (
        <EmailSubmission
          onEmailSubmission={async (email) => {
            setIsProcessing(true);

            await submitEmail(
              { email, role: UserRoles.CUSTOMER },
              onEmailSubmissionSuccess,
              handleBusinessErroṛ
            );

            setIsProcessing(false);
          }}
        ></EmailSubmission>
      )
    },
    {
      title: "Verify email OTP",
      content: OtpSubmission({
        onOtpSubmission,
        onOtpResend,
        otpStatus,
        otpCanBeResentAt,
        errorMessage: null
      })
    },
    {
      title: "Submit Phone number",
      content: (
        <PhoneNumberSubmission
          onPhoneNumberSubmission={(value) => {
            onPhoneNumberSubmission(value);
          }}
        ></PhoneNumberSubmission>
      )
    },
    {
      title: "Submit Password",
      content: (
        <PasswordSubmission
          onPasswordSubmission={(value) => {
            onPasswordSubmission(value);
          }}
        ></PasswordSubmission>
      )
    }
  ];

  return (
    <Row justify="center">
      <Col xs={20} sm={20} md={18} lg={12}>
        <Spin spinning={isProcessing}>
          <Steps current={currentStep} items={steps} />
          <div>{steps[currentStep].content}</div>
          <Button
            disabled={currentStep === 0}
            style={{
              margin: "0 8px"
            }}
            onClick={() => prev()}
          >
            Previous
          </Button>
          <Button
            disabled={
              currentStep === farthestStep || currentStep === steps.length - 1
            }
            type="primary"
            onClick={() => next()}
          >
            Next
          </Button>
        </Spin>
      </Col>
    </Row>
  );
}

export default Registration;
