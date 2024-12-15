package hung.megamarketv2.unit.ecommerce.user.registration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import hung.megamarketv2.common.account.ErrorCodes.AccountRepositoryErrorCodes;
import hung.megamarketv2.common.account.repositories.AccountRepository;
import hung.megamarketv2.common.generic.enums.UserRole;
import hung.megamarketv2.common.generic.enums.OtpEnums.OtpType;
import hung.megamarketv2.common.generic.enums.RegistrationEnums.EmailVerificationStatus;
import hung.megamarketv2.common.generic.enums.RegistrationEnums.PhoneNumberVerificationStatus;
import hung.megamarketv2.common.generic.enums.RegistrationEnums.RegistrationStatus;
import hung.megamarketv2.common.generic.enums.RegistrationEnums.RegistrationSteps;
import hung.megamarketv2.common.generic.exceptions.CommonExceptions.UnexpectedErrorException;
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
import hung.megamarketv2.ecommerce.modules.user.modules.registration.Dto.EmailSubmissionResult;
import hung.megamarketv2.ecommerce.modules.user.modules.registration.ErrorCodes.RegistrationRepositoryErrorCodes;
import hung.megamarketv2.ecommerce.modules.user.modules.registration.ErrorCodes.RegistrationServiceErrorCodes;
import hung.megamarketv2.ecommerce.modules.user.modules.registration.repositories.RegistrationRepository;
import hung.megamarketv2.ecommerce.modules.user.modules.registration.services.RegistrationService;
import hung.megamarketv2.ecommerce.modules.user.modules.registration.services.RegistrationServiceImp;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

        private static final String VALID_EMAIL = "mail@gmail.com";
        private static final String VALID_PHONE_NUMBER = "+84936484025";
        private static final String VALID_PASSWORD = "Password+123$";
        private static final String REGISTRATION_TEXT_ID = "registrationTextId";
        private static final String OTP = "otp";
        private static final UserRole ROLE = UserRole.CUSTOMER;
        private static final int REQUEST_TIMEOUT_IN_SECONDS = 1800;
        private static final int PASSWORD_TIMEOUT_IN_SECONDS = 15552000;
        public static final int DEFAULT_FIRST_NAME_LENGTH = 10;
        public static final int DEFAULT_LAST_NAME_LENGTH = 10;

        @Mock
        private OtpService mockedOtpService;

        @Mock
        private PasswordService mockedPasswordService;

        @Mock
        private UserRepository mockedUserRepository;

        @Mock
        private AccountRepository mockedAccountRepository;

        @Mock
        private PasswordRepository mockedPasswordRepository;

        @Mock
        private ProfileRepository mockedProfileRepository;

        @Mock
        private RegistrationRepository mockedRegistrationRepository;

        private RegistrationService service;

        @BeforeEach
        void setUp() {
                service = new RegistrationServiceImp(mockedOtpService, mockedPasswordService,
                                mockedAccountRepository, mockedPasswordRepository, mockedProfileRepository,
                                mockedUserRepository, mockedRegistrationRepository);
        }

        @Test
        void testSubmitEmailHappyPath() {
                when(mockedUserRepository.findByEmail(VALID_EMAIL))
                                .thenReturn(Result.ofError(UserRepositoryErrorCodes.USER_NOT_FOUND));

                RegistrationRequest request = new RegistrationRequest(VALID_EMAIL, ROLE,
                                LocalDateTime.now().plusSeconds(REQUEST_TIMEOUT_IN_SECONDS));
                request.setTextId(REGISTRATION_TEXT_ID);

                when(mockedRegistrationRepository.createRegistrationRequest(any(RegistrationRequest.class))).thenReturn(
                                Result.ofValue(request));

                OtpIdentity identity = new OtpIdentity(OtpType.EMAIL, RegistrationServiceImp.OTP_REFERENCE_TYPE,
                                request.getTextId());

                OtpSendingResult otpSendingResult = new OtpSendingResult("123456", 1);

                when(mockedOtpService.sendOtp(eq(identity), eq(VALID_EMAIL), eq(null)))
                                .thenReturn(Result.ofValue(otpSendingResult));

                Result<EmailSubmissionResult, RegistrationServiceErrorCodes> result = service.submitEmail(
                                VALID_EMAIL,
                                ROLE);

                assertTrue(result.isSuccessful);

                EmailSubmissionResult emailSubmissionResult = result.value;

                assertThat(emailSubmissionResult.registrationToken()).isEqualTo(REGISTRATION_TEXT_ID);
                assertThat(otpSendingResult.coolDownSeconds()).isEqualTo(emailSubmissionResult.otpCooldownSeconds());
        }

        @Test
        void testSubmitEmailButInvalidEmailShouldReturnError() {
                String inValidEmail = "invalidEmail";
                Result<EmailSubmissionResult, RegistrationServiceErrorCodes> result = service.submitEmail(
                                inValidEmail,
                                ROLE);

                assertFalse(result.isSuccessful);

                assertThat(result.error).isEqualTo(RegistrationServiceErrorCodes.INVALID_EMAIL);

        }

        @Test
        void testSubmitEmailButEmailAlreadyUsedShouldReturnError() {
                when(mockedUserRepository.findByEmail(VALID_EMAIL))
                                .thenReturn(Result.ofValue(new User()));

                Result<EmailSubmissionResult, RegistrationServiceErrorCodes> result = service.submitEmail(
                                VALID_EMAIL,
                                ROLE);

                assertFalse(result.isSuccessful);
                assertThat(result.error).isEqualTo(RegistrationServiceErrorCodes.EMAIL_HAS_BEEN_USED);
        }

        @Test
        void testSubmitEmailButUserRepositoryReturnsUnexpectedErrorShouldThrowException() {
                when(mockedUserRepository.findByEmail(VALID_EMAIL))
                                .thenReturn(Result.ofError(UserRepositoryErrorCodes.USER_ALREADY_EXISTS));

                assertThrows(UnexpectedErrorException.class, () -> service.submitEmail(VALID_EMAIL, ROLE));
        }

        @Test
        void testSubmitEmailButRequestCreationFailedShouldThrowException() {
                when(mockedUserRepository.findByEmail(VALID_EMAIL))
                                .thenReturn(Result.ofError(UserRepositoryErrorCodes.USER_NOT_FOUND));

                when(mockedRegistrationRepository.createRegistrationRequest(any(RegistrationRequest.class)))
                                .thenReturn(Result
                                                .ofError(RegistrationRepositoryErrorCodes.REQUEST_ALREADY_EXISTS));

                assertThrows(UnexpectedErrorException.class, () -> service.submitEmail(VALID_EMAIL, ROLE));
        }

        @Test
        void testSubmitEmailButSendOtpTooFrequentlyShouldReturnError() {
                when(mockedUserRepository.findByEmail(VALID_EMAIL))
                                .thenReturn(Result.ofError(UserRepositoryErrorCodes.USER_NOT_FOUND));

                RegistrationRequest request = new RegistrationRequest(VALID_EMAIL, ROLE,
                                LocalDateTime.now().plusSeconds(REQUEST_TIMEOUT_IN_SECONDS));
                request.setTextId(REGISTRATION_TEXT_ID);

                when(mockedRegistrationRepository.createRegistrationRequest(any(RegistrationRequest.class))).thenReturn(
                                Result.ofValue(request));

                OtpIdentity identity = new OtpIdentity(OtpType.EMAIL, RegistrationServiceImp.OTP_REFERENCE_TYPE,
                                request.getTextId());

                when(mockedOtpService.sendOtp(eq(identity), eq(VALID_EMAIL), eq(null)))
                                .thenReturn(Result.ofError(OtpServiceErrorCodes.RESEND_OTP_TOO_FREQUENTLY));

                Result<EmailSubmissionResult, RegistrationServiceErrorCodes> result = service.submitEmail(
                                VALID_EMAIL,
                                ROLE);

                assertFalse(result.isSuccessful);
                assertThat(result.error).isEqualTo(RegistrationServiceErrorCodes.RESEND_OTP_TOO_FREQUENTLY);
        }

        @Test
        void testVerifyEmailOtpHappyPath() {

                RegistrationRequest request = new RegistrationRequest(VALID_EMAIL, ROLE,
                                LocalDateTime.now().plusSeconds(REQUEST_TIMEOUT_IN_SECONDS));

                request.setTextId(REGISTRATION_TEXT_ID);

                when(mockedRegistrationRepository.findtByTextIdThenLockRequestInDatabase(REGISTRATION_TEXT_ID))
                                .thenReturn(
                                                Result.ofValue(request));

                OtpIdentity identity = new OtpIdentity(OtpType.EMAIL, RegistrationServiceImp.OTP_REFERENCE_TYPE,
                                REGISTRATION_TEXT_ID);

                when(mockedOtpService.verifyOtp(eq(OTP), eq(identity), eq(null), anyInt()))
                                .thenReturn(Outcome.ofSuccess());

                Outcome<RegistrationServiceErrorCodes> outcome = service.verifyEmailOtp(OTP, REGISTRATION_TEXT_ID);

                assertTrue(outcome.isSuccessful);
                assertThat(request.getStep()).isEqualTo(RegistrationSteps.SUBMIT_PHONE_NUMBER);

                assertThat(request.getEmailVerificationStatus())
                                .isEqualTo(EmailVerificationStatus.VERIFIED);
        }

        @Test
        void testVerifyEmailOtpButRequestNotFoundShouldReturnError() {
                RegistrationRequest request = new RegistrationRequest(VALID_EMAIL, ROLE,
                                LocalDateTime.now().plusSeconds(REQUEST_TIMEOUT_IN_SECONDS));
                request.setTextId(REGISTRATION_TEXT_ID);

                when(mockedRegistrationRepository.findtByTextIdThenLockRequestInDatabase(REGISTRATION_TEXT_ID))
                                .thenReturn(
                                                Result.ofError(
                                                                RegistrationRepositoryErrorCodes.REQUEST_NOT_FOUND));

                UnexpectedErrorException exception = assertThrows(UnexpectedErrorException.class,
                                () -> service.verifyEmailOtp(OTP, REGISTRATION_TEXT_ID));

                assertThat(exception.unexpectedError)
                                .isEqualTo(RegistrationRepositoryErrorCodes.REQUEST_NOT_FOUND.name());

                assertThat(request.getStep()).isEqualTo(RegistrationSteps.VERIFY_EMAIL_OTP);
                assertThat(request.getEmailVerificationStatus())
                                .isEqualTo(EmailVerificationStatus.NOT_VERIFIED);
        }

        @Test
        void testVerifyEmailOtpButInvalidRequestStatusShouldReturnError() {

                RegistrationRequest request = new RegistrationRequest(VALID_EMAIL, ROLE,
                                LocalDateTime.now().plusSeconds(REQUEST_TIMEOUT_IN_SECONDS));
                request.setTextId(REGISTRATION_TEXT_ID);
                request.setStatus(RegistrationStatus.APPROVED);

                when(mockedRegistrationRepository.findtByTextIdThenLockRequestInDatabase(REGISTRATION_TEXT_ID))
                                .thenReturn(
                                                Result.ofValue(request));

                Outcome<RegistrationServiceErrorCodes> outcome = service.verifyEmailOtp(OTP, REGISTRATION_TEXT_ID);

                assertFalse(outcome.isSuccessful);
                assertThat(outcome.error).isEqualTo(RegistrationServiceErrorCodes.INVALID_REQUEST_STATUS);

                assertThat(request.getStep()).isEqualTo(RegistrationSteps.VERIFY_EMAIL_OTP);
                assertThat(request.getEmailVerificationStatus())
                                .isEqualTo(EmailVerificationStatus.NOT_VERIFIED);

        }

        @Test
        void testVerifyEmailOtpButInvalidRequestStepShouldReturnError() {

                RegistrationRequest request = new RegistrationRequest(VALID_EMAIL, ROLE,
                                LocalDateTime.now().plusSeconds(REQUEST_TIMEOUT_IN_SECONDS));
                request.setTextId(REGISTRATION_TEXT_ID);
                request.setStep(RegistrationSteps.SUBMIT_EMAIL);

                when(mockedRegistrationRepository.findtByTextIdThenLockRequestInDatabase(REGISTRATION_TEXT_ID))
                                .thenReturn(
                                                Result.ofValue(request));

                Outcome<RegistrationServiceErrorCodes> outcome = service.verifyEmailOtp(OTP, REGISTRATION_TEXT_ID);

                assertFalse(outcome.isSuccessful);
                assertThat(outcome.error).isEqualTo(RegistrationServiceErrorCodes.INVALID_REQUEST_STEP);

                assertThat(request.getStep()).isEqualTo(RegistrationSteps.SUBMIT_EMAIL);
                assertThat(request.getEmailVerificationStatus())
                                .isEqualTo(EmailVerificationStatus.NOT_VERIFIED);

        }

        @Test
        void testVerifyEmailOtpButRequestExpiredShouldReturnError() {
                RegistrationRequest request = new RegistrationRequest(VALID_EMAIL, ROLE,
                                LocalDateTime.now().plusSeconds(REQUEST_TIMEOUT_IN_SECONDS));
                request.setTextId(REGISTRATION_TEXT_ID);
                request.setExpiredAt(LocalDateTime.now().minusSeconds(1));

                when(mockedRegistrationRepository.findtByTextIdThenLockRequestInDatabase(REGISTRATION_TEXT_ID))
                                .thenReturn(
                                                Result.ofValue(request));

                Outcome<RegistrationServiceErrorCodes> outcome = service.verifyEmailOtp(OTP, REGISTRATION_TEXT_ID);

                assertFalse(outcome.isSuccessful);
                assertThat(outcome.error).isEqualTo(RegistrationServiceErrorCodes.REQUEST_EXPIRED);

                assertThat(request.getStep()).isEqualTo(RegistrationSteps.VERIFY_EMAIL_OTP);
                assertThat(request.getEmailVerificationStatus())
                                .isEqualTo(EmailVerificationStatus.NOT_VERIFIED);

        }

        @Test
        void testVerifyEmailOtpButVerificationFailedShouldReturnError() {

                RegistrationRequest request = new RegistrationRequest(VALID_EMAIL, ROLE,
                                LocalDateTime.now().plusSeconds(REQUEST_TIMEOUT_IN_SECONDS));
                request.setTextId(REGISTRATION_TEXT_ID);

                when(mockedRegistrationRepository.findtByTextIdThenLockRequestInDatabase(REGISTRATION_TEXT_ID))
                                .thenReturn(
                                                Result.ofValue(request));

                OtpIdentity identity = new OtpIdentity(OtpType.EMAIL, RegistrationServiceImp.OTP_REFERENCE_TYPE,
                                request.getTextId());

                when(mockedOtpService.verifyOtp(eq(OTP), eq(identity), eq(null), anyInt()))
                                .thenReturn(Outcome.ofError(OtpServiceErrorCodes.OTP_NOT_MATCHED));

                Outcome<RegistrationServiceErrorCodes> outcome = service.verifyEmailOtp(OTP, REGISTRATION_TEXT_ID);

                assertFalse(outcome.isSuccessful);
                assertThat(outcome.error).isEqualTo(RegistrationServiceErrorCodes.OTP_NOT_MATCHED);

                assertThat(request.getStep()).isEqualTo(RegistrationSteps.VERIFY_EMAIL_OTP);
                assertThat(request.getEmailVerificationStatus())
                                .isEqualTo(EmailVerificationStatus.NOT_VERIFIED);

        }

        @Test
        void testVerifyEmailOtpButGotUnexpectedOtpErrorShouldReturnError() {
                RegistrationRequest request = new RegistrationRequest(VALID_EMAIL, ROLE,
                                LocalDateTime.now().plusSeconds(REQUEST_TIMEOUT_IN_SECONDS));
                request.setTextId(REGISTRATION_TEXT_ID);

                when(mockedRegistrationRepository.findtByTextIdThenLockRequestInDatabase(REGISTRATION_TEXT_ID))
                                .thenReturn(
                                                Result.ofValue(request));

                OtpIdentity identity = new OtpIdentity(OtpType.EMAIL, RegistrationServiceImp.OTP_REFERENCE_TYPE,
                                request.getTextId());

                when(mockedOtpService.verifyOtp(eq(OTP), eq(identity), eq(null), anyInt()))
                                .thenReturn(Outcome.ofError(OtpServiceErrorCodes.OTP_TYPE_NOT_SUPPORTED));

                assertThrows(UnexpectedErrorException.class, () -> service.verifyEmailOtp(OTP, REGISTRATION_TEXT_ID));

                assertThat(request.getStep()).isEqualTo(RegistrationSteps.VERIFY_EMAIL_OTP);
                assertThat(request.getEmailVerificationStatus())
                                .isEqualTo(EmailVerificationStatus.NOT_VERIFIED);
        }

        @Test
        void testSubmitPhoneNumberHappyPath() {
                when(mockedUserRepository.findByPhoneNumber(VALID_PHONE_NUMBER))
                                .thenReturn(Result.ofError(UserRepositoryErrorCodes.USER_NOT_FOUND));

                RegistrationRequest request = new RegistrationRequest(VALID_EMAIL, ROLE,
                                LocalDateTime.now().plusSeconds(REQUEST_TIMEOUT_IN_SECONDS));
                request.setTextId(REGISTRATION_TEXT_ID);
                request.setStep(RegistrationSteps.SUBMIT_PHONE_NUMBER);

                when(mockedRegistrationRepository.findtByTextIdThenLockRequestInDatabase(REGISTRATION_TEXT_ID))
                                .thenReturn(
                                                Result.ofValue(request));

                Outcome<RegistrationServiceErrorCodes> outcome = service.submitPhoneNumber(VALID_PHONE_NUMBER,
                                REGISTRATION_TEXT_ID);

                assertTrue(outcome.isSuccessful);
                assertThat(request.getStep()).isEqualTo(RegistrationSteps.SUBMIT_PASSWORD);

                assertThat(request.getPhoneNumberVerificationStatus())
                                .isEqualTo(PhoneNumberVerificationStatus.NOT_VERIFIED);
        }

        @Test
        void testSubmitPhoneNumberButInvalidPhoneNumberShouldReturnError() {

                Outcome<RegistrationServiceErrorCodes> outcome = service.submitPhoneNumber("1111111111",
                                REGISTRATION_TEXT_ID);

                assertFalse(outcome.isSuccessful);
                assertThat(outcome.error).isEqualTo(RegistrationServiceErrorCodes.INVALID_PHONE_NUMBER);
        }

        @Test
        void testSubmitPhoneNumberButRequestExpiredShouldReturnError() {
                RegistrationRequest request = new RegistrationRequest(VALID_EMAIL, ROLE,
                                LocalDateTime.now().minusSeconds(1));
                request.setTextId(REGISTRATION_TEXT_ID);
                request.setStep(RegistrationSteps.SUBMIT_PHONE_NUMBER);

                when(mockedRegistrationRepository.findtByTextIdThenLockRequestInDatabase(REGISTRATION_TEXT_ID))
                                .thenReturn(
                                                Result.ofValue(request));

                Outcome<RegistrationServiceErrorCodes> outcome = service.submitPhoneNumber(VALID_PHONE_NUMBER,
                                REGISTRATION_TEXT_ID);

                assertFalse(outcome.isSuccessful);

                assertThat(outcome.error).isEqualTo(RegistrationServiceErrorCodes.REQUEST_EXPIRED);
                assertThat(request.getStep()).isEqualTo(RegistrationSteps.SUBMIT_PHONE_NUMBER);

                assertThat(request.getPhoneNumberVerificationStatus())
                                .isEqualTo(PhoneNumberVerificationStatus.NOT_VERIFIED);
        }

        @Test
        void testSubmitPhoneNumberButInvalidRequestStepShouldReturnError() {
                RegistrationRequest request = new RegistrationRequest(VALID_EMAIL, ROLE,
                                LocalDateTime.now().plusSeconds(REQUEST_TIMEOUT_IN_SECONDS));

                request.setTextId(REGISTRATION_TEXT_ID);
                request.setStep(RegistrationSteps.VERIFY_EMAIL_OTP);

                when(mockedRegistrationRepository.findtByTextIdThenLockRequestInDatabase(REGISTRATION_TEXT_ID))
                                .thenReturn(
                                                Result.ofValue(request));

                Outcome<RegistrationServiceErrorCodes> outcome = service.submitPhoneNumber(VALID_PHONE_NUMBER,
                                REGISTRATION_TEXT_ID);

                assertFalse(outcome.isSuccessful);
                assertThat(outcome.error).isEqualTo(RegistrationServiceErrorCodes.INVALID_REQUEST_STEP);

                assertThat(request.getStep()).isEqualTo(RegistrationSteps.VERIFY_EMAIL_OTP);
                assertThat(request.getPhoneNumberVerificationStatus())
                                .isEqualTo(PhoneNumberVerificationStatus.NOT_VERIFIED);
        }

        @Test
        void testSubmitPhoneNumberButInvalidRequestStatusShouldReturnError() {
                RegistrationRequest request = new RegistrationRequest(VALID_EMAIL, ROLE,
                                LocalDateTime.now().plusSeconds(REQUEST_TIMEOUT_IN_SECONDS));
                request.setTextId(REGISTRATION_TEXT_ID);
                request.setStep(RegistrationSteps.SUBMIT_PHONE_NUMBER);
                request.setStatus(RegistrationStatus.REJECTED);

                when(mockedRegistrationRepository.findtByTextIdThenLockRequestInDatabase(REGISTRATION_TEXT_ID))
                                .thenReturn(
                                                Result.ofValue(request));

                Outcome<RegistrationServiceErrorCodes> outcome = service.submitPhoneNumber(VALID_PHONE_NUMBER,
                                REGISTRATION_TEXT_ID);

                assertFalse(outcome.isSuccessful);
                assertThat(outcome.error).isEqualTo(RegistrationServiceErrorCodes.INVALID_REQUEST_STATUS);

                assertThat(request.getStep()).isEqualTo(RegistrationSteps.SUBMIT_PHONE_NUMBER);
                assertThat(request.getPhoneNumberVerificationStatus())
                                .isEqualTo(PhoneNumberVerificationStatus.NOT_VERIFIED);
        }

        @Test
        void testSubmitPhoneNumberButGotUnexpectedErrorShouldThrowException() {
                RegistrationRequest request = new RegistrationRequest(VALID_EMAIL, ROLE,
                                LocalDateTime.now().plusSeconds(REQUEST_TIMEOUT_IN_SECONDS));
                request.setTextId(REGISTRATION_TEXT_ID);
                request.setStep(RegistrationSteps.SUBMIT_PHONE_NUMBER);

                when(mockedRegistrationRepository.findtByTextIdThenLockRequestInDatabase(REGISTRATION_TEXT_ID))
                                .thenReturn(
                                                Result.ofError(
                                                                RegistrationRepositoryErrorCodes.REQUEST_ALREADY_EXISTS));

                assertThrows(UnexpectedErrorException.class,
                                () -> service.submitPhoneNumber(VALID_PHONE_NUMBER, REGISTRATION_TEXT_ID));

                assertThat(request.getStep()).isEqualTo(RegistrationSteps.SUBMIT_PHONE_NUMBER);

                assertThat(request.getPhoneNumberVerificationStatus())
                                .isEqualTo(PhoneNumberVerificationStatus.NOT_VERIFIED);
        }

        @Test
        void testSubmitPhoneNumberButPhoneNumberAlreadyUsedShouldReturnError() {

                RegistrationRequest request = new RegistrationRequest(VALID_EMAIL, ROLE,
                                LocalDateTime.now().plusSeconds(REQUEST_TIMEOUT_IN_SECONDS));
                request.setTextId(REGISTRATION_TEXT_ID);
                request.setStep(RegistrationSteps.SUBMIT_PHONE_NUMBER);

                when(mockedRegistrationRepository.findtByTextIdThenLockRequestInDatabase(REGISTRATION_TEXT_ID))
                                .thenReturn(
                                                Result.ofValue(request));

                when(mockedUserRepository.findByPhoneNumber(VALID_PHONE_NUMBER))
                                .thenReturn(Result.ofValue(new User()));

                Outcome<RegistrationServiceErrorCodes> outcome = service.submitPhoneNumber(VALID_PHONE_NUMBER,
                                REGISTRATION_TEXT_ID);

                assertFalse(outcome.isSuccessful);
                assertThat(outcome.error).isEqualTo(RegistrationServiceErrorCodes.PHONE_NUMBER_HAS_BEEN_USED);

                assertThat(request.getStep()).isEqualTo(RegistrationSteps.SUBMIT_PHONE_NUMBER);

                assertThat(request.getPhoneNumberVerificationStatus())
                                .isEqualTo(PhoneNumberVerificationStatus.NOT_VERIFIED);
        }

        @Test
        void testSubmitPasswordHappyPath() {
                RegistrationRequest request = new RegistrationRequest(VALID_EMAIL, ROLE,
                                LocalDateTime.now().plusSeconds(REQUEST_TIMEOUT_IN_SECONDS));
                request.setTextId(REGISTRATION_TEXT_ID);
                request.setStep(RegistrationSteps.SUBMIT_PASSWORD);
                request.setEmailVerificationStatus(EmailVerificationStatus.VERIFIED);
                request.setPhoneNumber(VALID_PHONE_NUMBER);

                when(mockedPasswordService.validatePassword(VALID_PASSWORD)).thenReturn(true);

                when(mockedRegistrationRepository.findtByTextIdThenLockRequestInDatabase(REGISTRATION_TEXT_ID))
                                .thenReturn(
                                                Result.ofValue(request));

                User user = new User(VALID_EMAIL, VALID_PHONE_NUMBER, ROLE);

                when(mockedUserRepository.createUser(user)).thenReturn(Result.ofValue(user));

                when(mockedPasswordService.encodePassword(VALID_PASSWORD)).thenReturn(VALID_PASSWORD);

                Password password = new Password(user, VALID_PASSWORD,
                                LocalDateTime.now().plusSeconds(PASSWORD_TIMEOUT_IN_SECONDS));

                when(mockedPasswordRepository.createPassword(argThat(p -> {
                        return p.getValue().equals(VALID_PASSWORD) && p.getUser().equals(user);
                }))).thenReturn(Result.ofValue(password));

                Account account = new Account(user);

                when(mockedAccountRepository.createAccount(account)).thenReturn(Result.ofValue(account));

                String randomFirstName = RandomStringUtils.randomAlphabetic(DEFAULT_FIRST_NAME_LENGTH);
                String randomlastName = RandomStringUtils.randomAlphabetic(DEFAULT_LAST_NAME_LENGTH);
                Profile profile = new Profile(user, randomFirstName, randomlastName);

                when(mockedProfileRepository.createProfile(argThat(pf -> {
                        return pf.getFirstName() != null && pf.getLastName() != null
                                        && pf.getUser().equals(user);
                }))).thenReturn(Result.ofValue(profile));

                Outcome<RegistrationServiceErrorCodes> outcome = service.submitPassword(
                                VALID_PASSWORD,
                                REGISTRATION_TEXT_ID);

                assertTrue(outcome.isSuccessful);

                assertThat(request.getStep()).isEqualTo(RegistrationSteps.COMPLETED);
                assertThat(request.getStatus()).isEqualTo(RegistrationStatus.APPROVED);

                assertThat(request.getPhoneNumberVerificationStatus())
                                .isEqualTo(PhoneNumberVerificationStatus.NOT_VERIFIED);

                verify(mockedUserRepository, times(1)).createUser(user);
                verify(mockedPasswordRepository, times(1)).createPassword(argThat(p -> {
                        return p.getValue().equals(VALID_PASSWORD) && p.getUser().equals(user);
                }));
                verify(mockedAccountRepository, times(1)).createAccount(account);
                verify(mockedProfileRepository, times(1)).createProfile(argThat(pf -> {
                        return pf.getFirstName() != null && pf.getLastName() != null
                                        && pf.getUser().equals(user);
                }));
        }

        @Test
        void testSubmitPasswordButInvalidPasswordShouldReturnError() {

                when(mockedPasswordService.validatePassword(VALID_PASSWORD)).thenReturn(false);

                Outcome<RegistrationServiceErrorCodes> outcome = service.submitPassword(
                                VALID_PASSWORD,
                                REGISTRATION_TEXT_ID);

                assertFalse(outcome.isSuccessful);
                assertThat(outcome.error).isEqualTo(RegistrationServiceErrorCodes.INVALID_PASSWORD);

        }

        @Test
        void testSubmitPasswordButRequestNotFoundShouldReturnError() {
                when(mockedPasswordService.validatePassword(VALID_PASSWORD)).thenReturn(true);

                when(mockedRegistrationRepository.findtByTextIdThenLockRequestInDatabase(REGISTRATION_TEXT_ID))
                                .thenReturn(
                                                Result.ofError(
                                                                RegistrationRepositoryErrorCodes.REQUEST_NOT_FOUND));

                UnexpectedErrorException exception = assertThrows(UnexpectedErrorException.class,
                                () -> service.submitPassword(
                                                VALID_PASSWORD,
                                                REGISTRATION_TEXT_ID));

                assertThat(exception.unexpectedError)
                                .isEqualTo(RegistrationRepositoryErrorCodes.REQUEST_NOT_FOUND.name());

        }

        @Test
        void testSubmitPasswordButRequestExpiredShouldReturnError() {

                RegistrationRequest request = new RegistrationRequest(VALID_EMAIL, ROLE,
                                LocalDateTime.now().minusSeconds(1));
                request.setStep(RegistrationSteps.SUBMIT_PASSWORD);
                request.setEmailVerificationStatus(EmailVerificationStatus.VERIFIED);
                request.setPhoneNumber(VALID_PHONE_NUMBER);

                when(mockedPasswordService.validatePassword(VALID_PASSWORD)).thenReturn(true);

                when(mockedRegistrationRepository.findtByTextIdThenLockRequestInDatabase(REGISTRATION_TEXT_ID))
                                .thenReturn(
                                                Result.ofValue(request));

                Outcome<RegistrationServiceErrorCodes> outcome = service.submitPassword(
                                VALID_PASSWORD,
                                REGISTRATION_TEXT_ID);

                assertFalse(outcome.isSuccessful);
                assertThat(outcome.error).isEqualTo(RegistrationServiceErrorCodes.REQUEST_EXPIRED);

                assertThat(request.getStep()).isEqualTo(RegistrationSteps.SUBMIT_PASSWORD);
                assertThat(request.getStatus()).isEqualTo(RegistrationStatus.PROCESSING);

        }

        @Test
        void testSubmitPasswordButInvalidRequestStatusShouldReturnError() {

                RegistrationRequest request = new RegistrationRequest(VALID_EMAIL, ROLE,
                                LocalDateTime.now().plusSeconds(REQUEST_TIMEOUT_IN_SECONDS));
                request.setStep(RegistrationSteps.SUBMIT_PASSWORD);
                request.setStatus(RegistrationStatus.REJECTED);
                request.setEmailVerificationStatus(EmailVerificationStatus.VERIFIED);
                request.setPhoneNumber(VALID_PHONE_NUMBER);

                when(mockedPasswordService.validatePassword(VALID_PASSWORD)).thenReturn(true);

                when(mockedRegistrationRepository.findtByTextIdThenLockRequestInDatabase(REGISTRATION_TEXT_ID))
                                .thenReturn(
                                                Result.ofValue(request));

                Outcome<RegistrationServiceErrorCodes> outcome = service.submitPassword(
                                VALID_PASSWORD,
                                REGISTRATION_TEXT_ID);

                assertFalse(outcome.isSuccessful);
                assertThat(outcome.error).isEqualTo(RegistrationServiceErrorCodes.INVALID_REQUEST_STATUS);

                assertThat(request.getStep()).isEqualTo(RegistrationSteps.SUBMIT_PASSWORD);
                assertThat(request.getStatus()).isEqualTo(RegistrationStatus.REJECTED);
        }

        @Test
        void testSubmitPasswordButInvalidRequestStepShouldReturnError() {
                RegistrationRequest request = new RegistrationRequest(VALID_EMAIL, ROLE,
                                LocalDateTime.now().plusSeconds(REQUEST_TIMEOUT_IN_SECONDS));
                request.setStep(RegistrationSteps.SUBMIT_PHONE_NUMBER);
                request.setEmailVerificationStatus(EmailVerificationStatus.VERIFIED);
                request.setPhoneNumber(VALID_PHONE_NUMBER);

                when(mockedPasswordService.validatePassword(VALID_PASSWORD)).thenReturn(true);

                when(mockedRegistrationRepository.findtByTextIdThenLockRequestInDatabase(REGISTRATION_TEXT_ID))
                                .thenReturn(
                                                Result.ofValue(request));

                Outcome<RegistrationServiceErrorCodes> outcome = service.submitPassword(
                                VALID_PASSWORD,
                                REGISTRATION_TEXT_ID);

                assertFalse(outcome.isSuccessful);
                assertThat(outcome.error).isEqualTo(RegistrationServiceErrorCodes.INVALID_REQUEST_STEP);

                assertThat(request.getStep()).isEqualTo(RegistrationSteps.SUBMIT_PHONE_NUMBER);
                assertThat(request.getStatus()).isEqualTo(RegistrationStatus.PROCESSING);
        }

        @Test
        void testSubmitPasswordButMissingEmailShouldReturnError() {
                RegistrationRequest request = new RegistrationRequest(null, ROLE,
                                LocalDateTime.now().plusSeconds(REQUEST_TIMEOUT_IN_SECONDS));
                request.setStep(RegistrationSteps.SUBMIT_PASSWORD);
                request.setEmailVerificationStatus(EmailVerificationStatus.VERIFIED);
                request.setPhoneNumber(VALID_PHONE_NUMBER);

                when(mockedPasswordService.validatePassword(VALID_PASSWORD)).thenReturn(true);

                when(mockedRegistrationRepository.findtByTextIdThenLockRequestInDatabase(REGISTRATION_TEXT_ID))
                                .thenReturn(
                                                Result.ofValue(request));

                Outcome<RegistrationServiceErrorCodes> outcome = service.submitPassword(
                                VALID_PASSWORD,
                                REGISTRATION_TEXT_ID);

                assertFalse(outcome.isSuccessful);
                assertThat(outcome.error).isEqualTo(RegistrationServiceErrorCodes.MISSING_EMAIL);

                assertThat(request.getStep()).isEqualTo(RegistrationSteps.SUBMIT_PASSWORD);
                assertThat(request.getStatus()).isEqualTo(RegistrationStatus.PROCESSING);
        }

        @Test
        void testSubmitPasswordButMissingPhoneNumberShouldReturnError() {
                RegistrationRequest request = new RegistrationRequest(VALID_EMAIL, ROLE,
                                LocalDateTime.now().plusSeconds(REQUEST_TIMEOUT_IN_SECONDS));
                request.setStep(RegistrationSteps.SUBMIT_PASSWORD);
                request.setEmailVerificationStatus(EmailVerificationStatus.VERIFIED);

                when(mockedPasswordService.validatePassword(VALID_PASSWORD)).thenReturn(true);

                when(mockedRegistrationRepository.findtByTextIdThenLockRequestInDatabase(REGISTRATION_TEXT_ID))
                                .thenReturn(
                                                Result.ofValue(request));

                Outcome<RegistrationServiceErrorCodes> outcome = service.submitPassword(
                                VALID_PASSWORD,
                                REGISTRATION_TEXT_ID);

                assertFalse(outcome.isSuccessful);
                assertThat(outcome.error).isEqualTo(RegistrationServiceErrorCodes.MISSING_PHONE_NUMBER);

                assertThat(request.getStep()).isEqualTo(RegistrationSteps.SUBMIT_PASSWORD);
                assertThat(request.getStatus()).isEqualTo(RegistrationStatus.PROCESSING);
        }

        @Test
        void testSubmitPasswordButEmailNotVerifiedShouldReturnError() {
                RegistrationRequest request = new RegistrationRequest(VALID_EMAIL, ROLE,
                                LocalDateTime.now().plusSeconds(REQUEST_TIMEOUT_IN_SECONDS));
                request.setStep(RegistrationSteps.SUBMIT_PASSWORD);
                request.setPhoneNumber(VALID_PHONE_NUMBER);

                when(mockedPasswordService.validatePassword(VALID_PASSWORD)).thenReturn(true);

                when(mockedRegistrationRepository.findtByTextIdThenLockRequestInDatabase(REGISTRATION_TEXT_ID))
                                .thenReturn(
                                                Result.ofValue(request));

                Outcome<RegistrationServiceErrorCodes> outcome = service.submitPassword(
                                VALID_PASSWORD,
                                REGISTRATION_TEXT_ID);

                assertFalse(outcome.isSuccessful);
                assertThat(outcome.error).isEqualTo(RegistrationServiceErrorCodes.EMAIL_NOT_VERIFIED);

                assertThat(request.getStep()).isEqualTo(RegistrationSteps.SUBMIT_PASSWORD);
                assertThat(request.getStatus()).isEqualTo(RegistrationStatus.PROCESSING);
        }

        @Test
        void testSubmitPasswordButFailedToCreateUserShouldThrowException() {
                RegistrationRequest request = new RegistrationRequest(VALID_EMAIL, ROLE,
                                LocalDateTime.now().plusSeconds(REQUEST_TIMEOUT_IN_SECONDS));
                request.setEmailVerificationStatus(EmailVerificationStatus.VERIFIED);
                request.setStep(RegistrationSteps.SUBMIT_PASSWORD);
                request.setPhoneNumber(VALID_PHONE_NUMBER);

                when(mockedPasswordService.validatePassword(VALID_PASSWORD)).thenReturn(true);

                User user = new User(VALID_EMAIL, VALID_PHONE_NUMBER, ROLE);

                when(mockedRegistrationRepository.findtByTextIdThenLockRequestInDatabase(REGISTRATION_TEXT_ID))
                                .thenReturn(
                                                Result.ofValue(request));

                when(mockedUserRepository.createUser(user))
                                .thenReturn(Result.ofError(UserRepositoryErrorCodes.USER_ALREADY_EXISTS));

                UnexpectedErrorException exception = assertThrows(UnexpectedErrorException.class,
                                () -> service.submitPassword(
                                                VALID_PASSWORD,
                                                REGISTRATION_TEXT_ID));

                assertThat(exception.unexpectedError).isEqualTo(UserRepositoryErrorCodes.USER_ALREADY_EXISTS.name());

                assertThat(request.getStep()).isEqualTo(RegistrationSteps.SUBMIT_PASSWORD);
                assertThat(request.getStatus()).isEqualTo(RegistrationStatus.PROCESSING);
        }

        @Test
        void testSubmitPasswordButFailedToCreatePasswordShouldThrowException() {

                when(mockedPasswordService.validatePassword(VALID_PASSWORD)).thenReturn(true);

                RegistrationRequest request = new RegistrationRequest(VALID_EMAIL, ROLE,
                                LocalDateTime.now().plusSeconds(REQUEST_TIMEOUT_IN_SECONDS));
                request.setPhoneNumber(VALID_PHONE_NUMBER);
                request.setEmailVerificationStatus(EmailVerificationStatus.VERIFIED);
                request.setStep(RegistrationSteps.SUBMIT_PASSWORD);

                when(mockedRegistrationRepository.findtByTextIdThenLockRequestInDatabase(REGISTRATION_TEXT_ID))
                                .thenReturn(
                                                Result.ofValue(request));

                User user = new User(VALID_EMAIL, VALID_PHONE_NUMBER, ROLE);

                when(mockedUserRepository.createUser(user))
                                .thenReturn(Result.ofValue(user));

                when(mockedPasswordService.encodePassword(VALID_PASSWORD)).thenReturn(VALID_PASSWORD);

                when(mockedPasswordRepository.createPassword(argThat(p -> {
                        return p.getValue().equals(VALID_PASSWORD) && p.getUser().equals(user);
                })))
                                .thenReturn(Result
                                                .ofError(PasswordRepositoryErrorCodes.PASSWORD_ALREADY_EXISTS));

                UnexpectedErrorException exception = assertThrows(UnexpectedErrorException.class,
                                () -> service.submitPassword(
                                                VALID_PASSWORD,
                                                REGISTRATION_TEXT_ID));

                assertThat(exception.unexpectedError)
                                .isEqualTo(PasswordRepositoryErrorCodes.PASSWORD_ALREADY_EXISTS.name());

                assertThat(request.getStep()).isEqualTo(RegistrationSteps.SUBMIT_PASSWORD);
                assertThat(request.getStatus()).isEqualTo(RegistrationStatus.PROCESSING);
        }

        @Test
        void testSubmitPasswordButFailedToCreateAccountShouldThrowException() {
                when(mockedPasswordService.validatePassword(VALID_PASSWORD)).thenReturn(true);

                RegistrationRequest request = new RegistrationRequest(VALID_EMAIL, ROLE,
                                LocalDateTime.now().plusSeconds(REQUEST_TIMEOUT_IN_SECONDS));
                request.setPhoneNumber(VALID_PHONE_NUMBER);

                request.setEmailVerificationStatus(EmailVerificationStatus.VERIFIED);
                request.setStep(RegistrationSteps.SUBMIT_PASSWORD);

                when(mockedRegistrationRepository.findtByTextIdThenLockRequestInDatabase(REGISTRATION_TEXT_ID))
                                .thenReturn(
                                                Result.ofValue(request));

                User user = new User(VALID_EMAIL, VALID_PHONE_NUMBER, ROLE);

                when(mockedUserRepository.createUser(user))
                                .thenReturn(Result.ofValue(user));

                Password password = new Password(user, VALID_PASSWORD,
                                LocalDateTime.now().plusSeconds(PASSWORD_TIMEOUT_IN_SECONDS));

                when(mockedPasswordService.encodePassword(VALID_PASSWORD)).thenReturn(VALID_PASSWORD);

                when(mockedPasswordRepository.createPassword(argThat(p -> {
                        return p.getValue().equals(VALID_PASSWORD) && p.getUser().equals(user);
                })))
                                .thenReturn(Result.ofValue(password));

                Account account = new Account(user);

                when(mockedAccountRepository.createAccount(account))
                                .thenReturn(Result.ofError(AccountRepositoryErrorCodes.ACCOUNT_ALREADY_EXISTS));

                UnexpectedErrorException exception = assertThrows(UnexpectedErrorException.class,
                                () -> service.submitPassword(
                                                VALID_PASSWORD,
                                                REGISTRATION_TEXT_ID));

                assertThat(exception.unexpectedError)
                                .isEqualTo(AccountRepositoryErrorCodes.ACCOUNT_ALREADY_EXISTS.name());

                assertThat(request.getStep()).isEqualTo(RegistrationSteps.SUBMIT_PASSWORD);
                assertThat(request.getStatus()).isEqualTo(RegistrationStatus.PROCESSING);
        }

        @Test
        void testSubmitPasswordButFailedToCreateProfileShouldThrowException() {
                when(mockedPasswordService.validatePassword(VALID_PASSWORD)).thenReturn(true);

                RegistrationRequest request = new RegistrationRequest(VALID_EMAIL, ROLE,
                                LocalDateTime.now().plusSeconds(REQUEST_TIMEOUT_IN_SECONDS));
                request.setPhoneNumber(VALID_PHONE_NUMBER);

                request.setEmailVerificationStatus(EmailVerificationStatus.VERIFIED);
                request.setStep(RegistrationSteps.SUBMIT_PASSWORD);

                when(mockedRegistrationRepository.findtByTextIdThenLockRequestInDatabase(REGISTRATION_TEXT_ID))
                                .thenReturn(
                                                Result.ofValue(request));

                User user = new User(VALID_EMAIL, VALID_PHONE_NUMBER, ROLE);

                when(mockedUserRepository.createUser(user))
                                .thenReturn(Result.ofValue(user));

                Password password = new Password(user, VALID_PASSWORD,
                                LocalDateTime.now().plusSeconds(PASSWORD_TIMEOUT_IN_SECONDS));

                when(mockedPasswordService.encodePassword(VALID_PASSWORD)).thenReturn(VALID_PASSWORD);

                when(mockedPasswordRepository.createPassword(argThat(p -> {
                        return p.getValue().equals(VALID_PASSWORD) && p.getUser().equals(user);
                })))
                                .thenReturn(Result.ofValue(password));

                Account account = new Account(user);

                when(mockedAccountRepository.createAccount(account))
                                .thenReturn(Result.ofValue(account));

                when(mockedProfileRepository.createProfile(argThat(pf -> {
                        return pf.getFirstName() != null && pf.getLastName() != null
                                        && pf.getUser().equals(user);
                })))
                                .thenReturn(Result.ofError(ProfileRepositoryErrorCodes.PROFILE_ALREADY_EXISTS));

                UnexpectedErrorException exception = assertThrows(UnexpectedErrorException.class,
                                () -> service.submitPassword(
                                                VALID_PASSWORD,
                                                REGISTRATION_TEXT_ID));

                assertThat(exception.unexpectedError)
                                .isEqualTo(ProfileRepositoryErrorCodes.PROFILE_ALREADY_EXISTS.name());

                assertThat(request.getStep()).isEqualTo(RegistrationSteps.SUBMIT_PASSWORD);
                assertThat(request.getStatus()).isEqualTo(RegistrationStatus.PROCESSING);
        }

}
