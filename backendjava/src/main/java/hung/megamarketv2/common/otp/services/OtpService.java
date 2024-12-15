package hung.megamarketv2.common.otp.services;

import hung.megamarketv2.common.generic.outcomes.Outcome;
import hung.megamarketv2.common.generic.results.Result;
import hung.megamarketv2.common.otp.Dto.OtpIdentity;
import hung.megamarketv2.common.otp.Dto.OtpSendingResult;
import hung.megamarketv2.common.otp.ErrorCodes.OtpServiceErrorCodes;

public interface OtpService {

        public Result<OtpSendingResult, OtpServiceErrorCodes> sendOtp(OtpIdentity otpIdentity,
                        String destination,
                        Long accountId);

        public Outcome<OtpServiceErrorCodes> verifyOtp(String otp, OtpIdentity otpIdentity, Long accountId,
                        int validationAttemptLimit);
}
