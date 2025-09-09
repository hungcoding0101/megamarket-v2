import { Button, Input, Spin } from "antd";
import { OtpFunctionStatus } from "../enums/otp";
import { useEffect, useRef, useState } from "react";

function timeToString(timeInSeconds: number): string {
  const seconds = timeInSeconds % 60;
  const timeInMinutes = Math.floor(timeInSeconds / 60);
  const minutes = timeInMinutes % 60;
  const timeInHours = Math.floor(timeInMinutes / 60);
  const strHour =
    timeInHours > 0 ? `${timeInHours.toString().padStart(2, "0")}:` : "";
  return `${strHour}${minutes.toString().padStart(2, "0")}:${seconds
    .toString()
    .padStart(2, "0")}`;
}

function OtpSubmission({
  onOtpSubmission,
  onOtpResend,
  otpStatus,
  otpCanBeResentAt,
  errorMessage
}: Readonly<{
  onOtpSubmission: (value: string) => void;
  onOtpResend: () => void;
  otpStatus: OtpFunctionStatus;
  otpCanBeResentAt?: Date;
  errorMessage: string | null;
}>) {
  const [cooldownSeconds, setcooldownSeconds] = useState(0);

  const isStopped = useRef(false);

  useEffect(() => {
    if (!otpCanBeResentAt) return;

    isStopped.current = false;
    recalculateCooldownSeconds();

    const intervalId = setInterval(() => {
      if (!isStopped.current) {
        recalculateCooldownSeconds();
      }
    }, 1000);

    return () => clearInterval(intervalId);
  }, [otpCanBeResentAt]);

  const recalculateCooldownSeconds = () => {
    const cooldownMilliseconds =
      otpCanBeResentAt!.getTime() - new Date(Date.now()).getTime();

    if (cooldownMilliseconds <= 0) isStopped.current = true;

    setcooldownSeconds(Math.floor(Math.max(cooldownMilliseconds, 0) / 1000));
  };

  const isBlockingUserInteraction = () =>
    otpStatus === OtpFunctionStatus.VERIFYING ||
    otpStatus === OtpFunctionStatus.RESENDING;

  const showOtpResendButton = () => {
    return cooldownSeconds > 0 ? (
      ` Resend OTP after ${timeToString(cooldownSeconds)}`
    ) : (
      <Button
        type="primary"
        disabled={cooldownSeconds > 0 || isBlockingUserInteraction()}
        onClick={onOtpResend}
      >
        Resend OTP
      </Button>
    );
  };

  return (
    <>
      <Input.OTP
        status={errorMessage ? "error" : ""}
        onChange={onOtpSubmission}
      ></Input.OTP>
      <Spin spinning={isBlockingUserInteraction()} />

      {otpCanBeResentAt ? showOtpResendButton() : <></>}
    </>
  );
}

export default OtpSubmission;
