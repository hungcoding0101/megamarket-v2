package hung.megamarketv2.ecommerce.modules.user.modules.customer.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hung.megamarketv2.common.generic.outcomes.Outcome;
import hung.megamarketv2.common.generic.results.Result;
import hung.megamarketv2.ecommerce.modules.security.SecurityUser;
import hung.megamarketv2.ecommerce.modules.user.modules.registration.Dto.EmailSubmissionRequest;
import hung.megamarketv2.ecommerce.modules.user.modules.registration.Dto.EmailSubmissionResult;
import hung.megamarketv2.ecommerce.modules.user.modules.registration.Dto.EmailVerificationRequest;
import hung.megamarketv2.ecommerce.modules.user.modules.registration.Dto.PasswordSubmissionRequest;
import hung.megamarketv2.ecommerce.modules.user.modules.registration.Dto.PhoneNumberSubmissionRequest;
import hung.megamarketv2.ecommerce.modules.user.modules.registration.ErrorCodes.RegistrationServiceErrorCodes;
import hung.megamarketv2.ecommerce.modules.user.modules.registration.services.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.security.oauth2.jwt.Jwt;

import java.security.Principal;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthenticationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/user/customer")
public class CustomerInfoController {

    @GetMapping("/home")
    public String getMethodName(Long userId) {

        return userId.toString();
    }

    @PostMapping("post")
    public String postMethodName(@RequestBody String entity) {
        // TODO: process POST request

        return entity;
    }

}
