package hung.megamarketv2.ecommerce.modules.user.modules.registration.services;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hung.megamarketv2.common.account.ErrorCodes.AccountRepositoryErrorCodes;
import hung.megamarketv2.common.account.repositories.AccountRepository;
import hung.megamarketv2.common.generic.enums.UserRole;
import hung.megamarketv2.common.generic.enums.GenericEnums.CountryCode;
import hung.megamarketv2.common.generic.enums.OtpEnums.OtpType;
import hung.megamarketv2.common.generic.enums.RegistrationEnums.EmailVerificationStatus;
import hung.megamarketv2.common.generic.enums.RegistrationEnums.RegistrationStatus;
import hung.megamarketv2.common.generic.enums.RegistrationEnums.RegistrationSteps;
import hung.megamarketv2.common.generic.models.User;
import hung.megamarketv2.common.generic.outcomes.Outcome;
import hung.megamarketv2.common.generic.models.Account;
import hung.megamarketv2.common.generic.models.Password;
import hung.megamarketv2.common.generic.models.Profile;
import hung.megamarketv2.common.generic.models.RegistrationRequest;
import hung.megamarketv2.common.generic.results.Result;
import hung.megamarketv2.common.otp.Dto.OtpIdentity;
import hung.megamarketv2.common.otp.Dto.OtpSendingResult;
import hung.megamarketv2.common.otp.ErrorCodes.OtpServiceErrorCodes;
import hung.megamarketv2.common.otp.services.OtpService;
import hung.megamarketv2.common.password.ErrorCodes.PasswordRepositoryErrorCodes;
import hung.megamarketv2.common.password.repositories.PasswordRepository;
import hung.megamarketv2.common.password.services.PasswordService;
import hung.megamarketv2.common.profile.ErrorCodes.ProfileRepositoryErrorCodes;
import hung.megamarketv2.common.profile.repositories.ProfileRepository;
import hung.megamarketv2.common.user.ErrorCodes.UserRepositoryErrorCodes;
import hung.megamarketv2.common.user.repositories.UserRepository;
import hung.megamarketv2.common.utils.EmailUtils;
import hung.megamarketv2.common.utils.PhoneNumerUtils;
import hung.megamarketv2.common.generic.exceptions.CommonExceptions.UnexpectedErrorException;

import hung.megamarketv2.ecommerce.modules.user.modules.registration.Dto.EmailSubmissionResult;
import hung.megamarketv2.ecommerce.modules.user.modules.registration.ErrorCodes.RegistrationRepositoryErrorCodes;
import hung.megamarketv2.ecommerce.modules.user.modules.registration.ErrorCodes.RegistrationServiceErrorCodes;
import hung.megamarketv2.ecommerce.modules.user.modules.registration.repositories.RegistrationRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegistrationServiceImp implements RegistrationService {

    public static final String OTP_REFERENCE_TYPE = "REGISTRATION";
    public static final int DEFAULT_FIRST_NAME_LENGTH = 10;
    public static final int DEFAULT_LAST_NAME_LENGTH = 10;

    private final OtpService otpService;

    private final PasswordService passwordService;

    private final AccountRepository accountRepository;

    private final PasswordRepository passwordRepository;

    private final ProfileRepository profileRepository;

    private final UserRepository userRepository;

    private final RegistrationRepository registrationRepository;

    private final EmailUtils emailUtils = new EmailUtils();

    private final PhoneNumerUtils phoneNumerUtils = new PhoneNumerUtils();

    @Value("${request-lifetime-in-seconds}")
    private int requestLifeTimeInSeconds;

    @Value("${password-lifetime-in-seconds}")
    private int passwordLifeTimeInSeconds;

    @Value("${otp.verification-attempt-limit}")
    private int otpVerificationAttepmtLimit;

    @Override
    public Result<EmailSubmissionResult, RegistrationServiceErrorCodes> submitEmail(String email,
            UserRole role) {

        boolean isValidEmail = emailUtils.validateEmail(email);
        if (!isValidEmail) {
            return Result.ofError(RegistrationServiceErrorCodes.INVALID_EMAIL);
        }

        Result<User, UserRepositoryErrorCodes> result = userRepository.findByEmail(email);

        boolean usedEmail = result.isSuccessful;
        if (usedEmail) {
            return Result.ofError(RegistrationServiceErrorCodes.EMAIL_HAS_BEEN_USED);
        }

        boolean isUnexpectedError = result.error != UserRepositoryErrorCodes.USER_NOT_FOUND;
        if (isUnexpectedError) {
            RegistrationServiceErrorCodes errorCode = parseErrorCode(result.error);
            return Result.ofError(errorCode);
        }

        LocalDateTime requestExpiredAt = LocalDateTime.now().plusSeconds(requestLifeTimeInSeconds);
        RegistrationRequest request = new RegistrationRequest(email, role, requestExpiredAt);

        Result<RegistrationRequest, RegistrationRepositoryErrorCodes> creationResult = registrationRepository
                .createRegistrationRequest(request);

        if (!creationResult.isSuccessful) {
            RegistrationServiceErrorCodes errorCode = parseErrorCode(creationResult.error);
            return Result.ofError(errorCode);
        }

        request = creationResult.value;

        OtpIdentity otpIdentity = new OtpIdentity(OtpType.EMAIL, OTP_REFERENCE_TYPE, request.getTextId());
        Result<OtpSendingResult, OtpServiceErrorCodes> sendingResult = otpService.sendOtp(otpIdentity, email,
                null);

        if (!sendingResult.isSuccessful) {
            RegistrationServiceErrorCodes errorCode = parseErrorCode(sendingResult.error);
            return Result.ofError(errorCode);
        }

        OtpSendingResult otpSendingResult = sendingResult.value;

        EmailSubmissionResult emailSubmissionResult = new EmailSubmissionResult(otpSendingResult.coolDownSeconds(),
                request.getTextId());

        return Result.ofValue(emailSubmissionResult);
    }

    @Transactional
    @Override
    public Outcome<RegistrationServiceErrorCodes> verifyEmailOtp(String otp, String registrationToken) {
        Result<RegistrationRequest, RegistrationRepositoryErrorCodes> requestFindingResult = registrationRepository
                .findtByTextIdThenLockRequestInDatabase(registrationToken);

        if (!requestFindingResult.isSuccessful) {
            RegistrationServiceErrorCodes errorCode = parseErrorCode(requestFindingResult.error);
            return Outcome.ofError(errorCode);
        }

        RegistrationRequest request = requestFindingResult.value;

        List<RegistrationStatus> expectedStatuses = Arrays.asList(RegistrationStatus.PROCESSING);

        List<RegistrationSteps> expectedSteps = Arrays.asList(RegistrationSteps.VERIFY_EMAIL_OTP,
                RegistrationSteps.SUBMIT_PHONE_NUMBER, RegistrationSteps.SUBMIT_PASSWORD);

        Outcome<RegistrationServiceErrorCodes> valitationOutcome = validateRegistrationRequest(request,
                expectedSteps, expectedStatuses);

        if (!valitationOutcome.isSuccessful) {
            return valitationOutcome;
        }

        OtpIdentity otpIdentity = new OtpIdentity(OtpType.EMAIL, OTP_REFERENCE_TYPE, request.getTextId());
        Outcome<OtpServiceErrorCodes> verificationResult = otpService.verifyOtp(otp, otpIdentity, null,
                otpVerificationAttepmtLimit);

        if (!verificationResult.isSuccessful) {
            RegistrationServiceErrorCodes errorCode = parseErrorCode(verificationResult.error);
            return Outcome.ofError(errorCode);
        }

        request.setEmailVerificationStatus(EmailVerificationStatus.VERIFIED);
        request.setStep(RegistrationSteps.SUBMIT_PHONE_NUMBER);

        return Outcome.ofSuccess();
    }

    @Transactional
    @Override
    public Outcome<RegistrationServiceErrorCodes> submitPhoneNumber(String phoneNumber,
            String registrationToken) {

        boolean isValidPhoneNumber = phoneNumerUtils.validatePhoneNumber(phoneNumber, CountryCode.VN);

        if (!isValidPhoneNumber) {
            return Outcome.ofError(RegistrationServiceErrorCodes.INVALID_PHONE_NUMBER);
        }

        Result<RegistrationRequest, RegistrationRepositoryErrorCodes> requestFindingResult = registrationRepository
                .findtByTextIdThenLockRequestInDatabase(registrationToken);

        if (!requestFindingResult.isSuccessful) {
            RegistrationServiceErrorCodes errorCode = parseErrorCode(requestFindingResult.error);
            return Outcome.ofError(errorCode);
        }

        RegistrationRequest request = requestFindingResult.value;

        List<RegistrationStatus> expectedStatuses = Arrays.asList(RegistrationStatus.PROCESSING);

        List<RegistrationSteps> expectedSteps = Arrays.asList(
                RegistrationSteps.SUBMIT_PHONE_NUMBER, RegistrationSteps.SUBMIT_PASSWORD);

        Outcome<RegistrationServiceErrorCodes> valitationOutcome = validateRegistrationRequest(request,
                expectedSteps, expectedStatuses);

        boolean isRequestInvalid = !valitationOutcome.isSuccessful;
        if (isRequestInvalid) {
            return valitationOutcome;
        }

        Result<User, UserRepositoryErrorCodes> findingUserByPhoneNumberResult = userRepository
                .findByPhoneNumber(phoneNumber);

        boolean isPhoneNumberUsed = findingUserByPhoneNumberResult.isSuccessful;
        if (isPhoneNumberUsed) {
            return Outcome.ofError(RegistrationServiceErrorCodes.PHONE_NUMBER_HAS_BEEN_USED);
        }

        boolean isUnexpectedError = findingUserByPhoneNumberResult.error != UserRepositoryErrorCodes.USER_NOT_FOUND;
        if (isUnexpectedError) {
            RegistrationServiceErrorCodes errorCode = parseErrorCode(findingUserByPhoneNumberResult.error);
            return Outcome.ofError(errorCode);
        }

        request.setPhoneNumber(phoneNumber);
        request.setStep(RegistrationSteps.SUBMIT_PASSWORD);

        return Outcome.ofSuccess();

    }

    @Transactional
    @Override
    public Outcome<RegistrationServiceErrorCodes> submitPassword(String rawPassword,
            String registrationToken) {
        boolean isPasswordValid = passwordService.validatePassword(rawPassword);

        if (!isPasswordValid) {
            return Outcome.ofError(RegistrationServiceErrorCodes.INVALID_PASSWORD);
        }

        Result<RegistrationRequest, RegistrationRepositoryErrorCodes> registrationRequestResult = registrationRepository
                .findtByTextIdThenLockRequestInDatabase(registrationToken);

        if (!registrationRequestResult.isSuccessful) {
            RegistrationServiceErrorCodes errorCode = parseErrorCode(registrationRequestResult.error);
            return Outcome.ofError(errorCode);
        }

        RegistrationRequest request = registrationRequestResult.value;

        List<RegistrationStatus> expectedStatuses = Arrays.asList(RegistrationStatus.PROCESSING);

        List<RegistrationSteps> expectedSteps = Arrays.asList(RegistrationSteps.SUBMIT_PASSWORD);

        Outcome<RegistrationServiceErrorCodes> valitationOutcome = validateRegistrationRequest(request,
                expectedSteps, expectedStatuses);

        boolean isInvalidRequest = !valitationOutcome.isSuccessful;
        if (isInvalidRequest) {
            return Outcome.ofError(valitationOutcome.error);
        }

        if (request.getEmail() == null) {
            return Outcome.ofError(RegistrationServiceErrorCodes.MISSING_EMAIL);
        }

        if (request.getPhoneNumber() == null) {
            return Outcome.ofError(RegistrationServiceErrorCodes.MISSING_PHONE_NUMBER);
        }

        if (request.getEmailVerificationStatus() != EmailVerificationStatus.VERIFIED) {
            return Outcome.ofError(RegistrationServiceErrorCodes.EMAIL_NOT_VERIFIED);
        }

        Outcome<RegistrationServiceErrorCodes> userCreationOutcome = createUser(request.getEmail(),
                request.getPhoneNumber(), request.getRole(), rawPassword);

        if (!userCreationOutcome.isSuccessful) {
            return userCreationOutcome;
        }

        request.setStatus(RegistrationStatus.APPROVED);
        request.setStep(RegistrationSteps.COMPLETED);

        return Outcome.ofSuccess();

    }

    private Outcome<RegistrationServiceErrorCodes> createUser(String email, String phoneNumber, UserRole role,
            String rawPassword) {
        User user = new User(email, phoneNumber, role);

        Result<User, UserRepositoryErrorCodes> userCreationResult = userRepository.createUser(user);

        if (!userCreationResult.isSuccessful) {
            RegistrationServiceErrorCodes errorCode = parseErrorCode(userCreationResult.error);
            return Outcome.ofError(errorCode);
        }

        String hashedPassword = passwordService.encodePassword(rawPassword);
        LocalDateTime passwordExpiredAt = LocalDateTime.now().plusSeconds(passwordLifeTimeInSeconds);
        Password password = new Password(user, hashedPassword, passwordExpiredAt);

        Result<Password, PasswordRepositoryErrorCodes> passwordCreationResult = passwordRepository
                .createPassword(password);

        if (!passwordCreationResult.isSuccessful){
            RegistrationServiceErrorCodes errorCode = parseErrorCode(passwordCreationResult.error);
            return Outcome.ofError(errorCode);
        }


        Account account = new Account(user);

        Result<Account, AccountRepositoryErrorCodes> accountCreationResult = accountRepository
                .createAccount(account);

        if (!accountCreationResult.isSuccessful){
            RegistrationServiceErrorCodes errorCode = parseErrorCode(accountCreationResult.error);
            return Outcome.ofError(errorCode);
        }

        String randomFirstName = RandomStringUtils.randomAlphabetic(DEFAULT_FIRST_NAME_LENGTH);
        String randomlastName = RandomStringUtils.randomAlphabetic(DEFAULT_LAST_NAME_LENGTH);
        Profile profile = new Profile(user, randomFirstName, randomlastName);

        Result<Profile, ProfileRepositoryErrorCodes> profileCreationResult = profileRepository
                .createProfile(profile);

        if (!profileCreationResult.isSuccessful){
            RegistrationServiceErrorCodes errorCode = parseErrorCode(profileCreationResult.error);
            return Outcome.ofError(errorCode);
        }

        return Outcome.ofSuccess();
    }

    private RegistrationServiceErrorCodes parseErrorCode(Enum<?> lowerLevelErrorCode) {
        if (lowerLevelErrorCode == OtpServiceErrorCodes.RESEND_OTP_TOO_FREQUENTLY) {
            return RegistrationServiceErrorCodes.RESEND_OTP_TOO_FREQUENTLY;
        }

        if (lowerLevelErrorCode == OtpServiceErrorCodes.OTP_EXPIRED) {
            return RegistrationServiceErrorCodes.OTP_EXPIRED;
        }

        if (lowerLevelErrorCode == OtpServiceErrorCodes.OTP_NOT_MATCHED) {
            return RegistrationServiceErrorCodes.OTP_NOT_MATCHED;
        }

        if (lowerLevelErrorCode == OtpServiceErrorCodes.REACHED_ATTEMPT_LIMIT) {
            return RegistrationServiceErrorCodes.REACHED_OTP_ATTEMPT_LIMIT;
        }

        throw new UnexpectedErrorException(lowerLevelErrorCode.name());
    }

    private Outcome<RegistrationServiceErrorCodes> validateRegistrationRequest(RegistrationRequest request,
            List<RegistrationSteps> expectedSteps, List<RegistrationStatus> expectedStatuses) {
        if (request.getExpiredAt().isBefore(LocalDateTime.now())) {
            return Outcome.ofError(RegistrationServiceErrorCodes.REQUEST_EXPIRED);
        }

        if (!expectedStatuses.contains(request.getStatus())) {
            return Outcome.ofError(RegistrationServiceErrorCodes.INVALID_REQUEST_STATUS);
        }

        if (!expectedSteps.contains(request.getStep())) {
            return Outcome.ofError(RegistrationServiceErrorCodes.INVALID_REQUEST_STEP);
        }

        return Outcome.ofSuccess();

    }

}
