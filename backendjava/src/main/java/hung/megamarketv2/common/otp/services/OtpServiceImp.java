package hung.megamarketv2.common.otp.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import hung.megamarketv2.common.account.ErrorCodes.AccountRepositoryErrorCodes;
import hung.megamarketv2.common.account.ErrorCodes.AccountServiceErrorCodes;
import hung.megamarketv2.common.account.Errors.AccountRepositoryError;
import hung.megamarketv2.common.account.repositories.AccountRepository;
import hung.megamarketv2.common.generic.constants.OtpConstants;
import hung.megamarketv2.common.generic.enums.OtpEnums.OtpType;
import hung.megamarketv2.common.generic.models.Account;
import hung.megamarketv2.common.generic.models.OtpRequest;
import hung.megamarketv2.common.generic.outcomes.Outcome;
import hung.megamarketv2.common.generic.results.Result;
import hung.megamarketv2.common.otp.ErrorCodes.OtpRepositoryErrorCodes;
import hung.megamarketv2.common.otp.ErrorCodes.OtpServiceErrorCodes;
import hung.megamarketv2.common.otp.Dto.OtpIdentity;
import hung.megamarketv2.common.otp.Dto.OtpSendingResult;
import hung.megamarketv2.common.otp.Dto.Otp;
import hung.megamarketv2.common.otp.repositories.OtpRepository;
import hung.megamarketv2.common.utils.NumberUtils;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.LockModeType;
import lombok.NonNull;

@Service
public class OtpServiceImp implements OtpService {

    private final PasswordEncoder passwordEncoder;

    private final AccountRepository accountRepository;

    private final PlatformTransactionManager transactionManager;

    private final OtpRepository otpRepository;

    @Value("${otp.length}")
    private int otpLength;

    @Value("${otp.lifetime-in-seconds}")
    private int otpLifetimeInSeconds;

    @Value("${otp.cooldown-seconds}")
    private int otpCooldownSeconds;

    public OtpServiceImp(OtpRepository otpRepository, PasswordEncoder passwordEncoder,
            AccountRepository accountRepository,
            PlatformTransactionManager transactionManager) {
        this.otpRepository = otpRepository;
        this.passwordEncoder = passwordEncoder;
        this.accountRepository = accountRepository;
        this.transactionManager = transactionManager;
    }

    private Otp generateOtp() {
        // StringBuilder otpCode = new StringBuilder();

        // for (int i = 0; i < otpLength; i++) {
        // int randomDigit =
        // NumberUtils.generateRandomDigitWithinRange(OtpConstants.OTP_MIN_DIGIT,
        // OtpConstants.OTP_MAX_DIGIT);
        // otpCode.append(randomDigit);
        // }

        // String rawValue = otpCode.toString();
        // String hashedValue = passwordEncoder.encode(rawValue);

        // return new Otp(rawValue, hashedValue);
        return null;
    }

    @Override
    public Result<OtpSendingResult, OtpServiceErrorCodes> sendOtp(OtpIdentity otpIdentity, String destination,
            @Nullable Long accountId) {
        // TransactionTemplate transactionTemplate = new
        // TransactionTemplate(this.transactionManager);
        // BaseResult<String, OtpServiceErrorCodes> result =
        // transactionTemplate.execute(status -> {

        // });
        return Result.ofValue(new OtpSendingResult("null", otpCooldownSeconds));

    }

    // @Transactional
    // private BaseResult<Otp, OtpServiceErrorCodes> generateOtp(OtpIdentity
    // otpIdentity) {
    // List<OtpRequest> relatedOtpRequests = otpRepository
    // .findOtpRequestByIdentity(otpIdentity).getValue();

    // boolean isResendingOtp = relatedOtpRequests.size() > 0;
    // }

    @Transactional
    private Result<Account, OtpServiceErrorCodes> lockAccountInDatabase(long id) {

        // Result<Account, AccountRepositoryError> result =
        // accountRepository.lockAccountInDatabase(id);

        // if (result.hasError()) {
        // AccountRepositoryErrorCodes errorCode = result.getError().errorCode();
        // switch (errorCode) {
        // case AccountRepositoryErrorCodes.ACCOUNT_NOT_FOUND ->
        // Result.ofError(new
        // OtpServiceErrorCodes(OtpServiceErrorCodes.ACCOUNT_NOT_FOUND));
        // }
        // }

        // return Result.ofValue(result.getValue());.
        throw new UnsupportedOperationException("Unimplemented method 'verifyOtp'");

    }

    private void sendOtpToDestination(String otpCode, OtpType otpType, String destination) {

    }

    @Override
    public Outcome<OtpServiceErrorCodes> verifyOtp(String otp, OtpIdentity otpIdentity, Long accountId,
            int validationAttemptLimit) {
        // TODO Auto-generated method stub
        return Outcome.ofSuccess();
    }

}
