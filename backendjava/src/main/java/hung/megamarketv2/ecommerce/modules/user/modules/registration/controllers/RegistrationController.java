package hung.megamarketv2.ecommerce.modules.user.modules.registration.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hung.megamarketv2.common.generic.outcomes.Outcome;
import hung.megamarketv2.common.generic.results.Result;
import hung.megamarketv2.ecommerce.modules.user.modules.registration.Dto.EmailSubmissionRequest;
import hung.megamarketv2.ecommerce.modules.user.modules.registration.Dto.EmailSubmissionResult;
import hung.megamarketv2.ecommerce.modules.user.modules.registration.Dto.EmailVerificationRequest;
import hung.megamarketv2.ecommerce.modules.user.modules.registration.Dto.PasswordSubmissionRequest;
import hung.megamarketv2.ecommerce.modules.user.modules.registration.Dto.PhoneNumberSubmissionRequest;
import hung.megamarketv2.ecommerce.modules.user.modules.registration.ErrorCodes.RegistrationServiceErrorCodes;
import hung.megamarketv2.ecommerce.modules.user.modules.registration.services.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user/registration")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService service;

    @PostMapping("/submit-email")
    public Result<EmailSubmissionResult, RegistrationServiceErrorCodes> postMethodName(
            @Valid @RequestBody EmailSubmissionRequest request) {
        return service.submitEmail(
                request.email(),
                request.role());

    }

    @PostMapping("/verify-email")
    public Outcome<RegistrationServiceErrorCodes> verifyEmail(
            @Valid @RequestBody EmailVerificationRequest request) {

        return service.verifyEmailOtp(request.otp(),
                request.registrationToken());

    }

    @PostMapping("/submit-phone-number")
    public Outcome<RegistrationServiceErrorCodes> submitPhoneNumer(
            @Valid @RequestBody PhoneNumberSubmissionRequest request) {

        return service.submitPhoneNumber(request.phoneNumber(),
                request.registrationToken());

    }

    @PostMapping("/submit-password")
    public Outcome<RegistrationServiceErrorCodes> submitPassword(
            @Valid @RequestBody PasswordSubmissionRequest request) {

        return service.submitPassword(request.password(), request.registrationToken());

    }

}
