package hung.megamarketv2.unit.ecommerce.user.registration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;

import hung.megamarketv2.common.generic.ExceptionHandling;
import hung.megamarketv2.common.generic.ExceptionHandling.BeanValidationError;
import hung.megamarketv2.common.generic.enums.UserRole;
import hung.megamarketv2.common.generic.outcomes.Outcome;
import hung.megamarketv2.common.generic.results.Result;
import hung.megamarketv2.ecommerce.modules.security.services.SecurityUserService;
import hung.megamarketv2.ecommerce.modules.user.modules.registration.Dto.EmailSubmissionRequest;
import hung.megamarketv2.ecommerce.modules.user.modules.registration.Dto.EmailSubmissionResult;
import hung.megamarketv2.ecommerce.modules.user.modules.registration.ErrorCodes.RegistrationServiceErrorCodes;
import hung.megamarketv2.ecommerce.modules.user.modules.registration.controllers.RegistrationController;
import hung.megamarketv2.ecommerce.modules.user.modules.registration.services.RegistrationService;
import hung.megamarketv2.ecommerce.modules.user.modules.registration.ErrorCodes.RegistrationServiceErrorCodes.StringRepresentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(controllers = { RegistrationController.class })
@Import({ TestConfig.class })
class RegistrationControllerTest {

        private static final String VALID_EMAIL = "mail@gmail.com";
        private static final String VALID_PHONE_NUMBER = "+84936484025";
        private static final String VALID_PASSWORD = "Password+123$";
        private static final String REGISTRATION_TEXT_ID = "registrationTextId";
        private static final UserRole ROLE = UserRole.CUSTOMER;

        @Autowired
        private MockMvc mockedMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private RegistrationService mockedRegistrationService;

        @MockBean
        private SecurityUserService mockedSecurityUserService;

        @MockBean
        private AuthorizationServerSettings authorizationServerSettings;

        @MockBean
        ExceptionHandling mockedExceptionHandling;

        @Test
        void testsubmitEmailHappyPath() throws Exception {
                Result<EmailSubmissionResult, RegistrationServiceErrorCodes> expectedResult = Result
                                .ofValue(new EmailSubmissionResult(1, REGISTRATION_TEXT_ID));

                when(mockedRegistrationService.submitEmail(VALID_EMAIL, ROLE)).thenReturn(expectedResult);

                EmailSubmissionRequest request = new EmailSubmissionRequest(VALID_EMAIL, ROLE);

                MvcResult mvcResult = mockedMvc
                                .perform(post("/user/registration/submit-email").contentType("application/json")
                                                .content(objectMapper.writeValueAsString(request)).with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                String expectedResponseBody = objectMapper.writeValueAsString(expectedResult);
                String actualResponseBody = mvcResult.getResponse().getContentAsString();

                assertThat(actualResponseBody).isEqualTo(expectedResponseBody);

        }

        @Test
        void testsubmitEmailButInvalidEmailShoudlReturnError() throws Exception {

                EmailSubmissionRequest request = new EmailSubmissionRequest("mail", ROLE);

                BeanValidationError beanValidationError = new BeanValidationError(
                                List.of(StringRepresentation.INVALID_EMAIL));

                Outcome<BeanValidationError> expectedOutcome = Outcome.ofError(beanValidationError);

                when(mockedExceptionHandling.handleBeanvalidationException(any(), any()))
                                .thenReturn(ResponseEntity.badRequest().body(expectedOutcome));

                MvcResult mvcResult = mockedMvc
                                .perform(post("/user/registration/submit-email").contentType("application/json")
                                                .content(objectMapper.writeValueAsString(request)).with(csrf()))
                                .andExpect(status().isBadRequest())
                                .andReturn();

                String expectedResponseBody = objectMapper.writeValueAsString(expectedOutcome);
                String actualResponseBody = mvcResult.getResponse().getContentAsString();

                assertThat(actualResponseBody).isEqualTo(expectedResponseBody);

        }

        @Test
        void testsubmitEmailButMissingEmailShoudlReturnError() throws Exception {

                EmailSubmissionRequest request = new EmailSubmissionRequest(null, ROLE);

                BeanValidationError beanValidationError = new BeanValidationError(
                                List.of(StringRepresentation.INVALID_EMAIL));

                Outcome<BeanValidationError> expectedOutcome = Outcome.ofError(beanValidationError);

                when(mockedExceptionHandling.handleBeanvalidationException(any(), any()))
                                .thenReturn(ResponseEntity.badRequest().body(expectedOutcome));

                MvcResult mvcResult = mockedMvc
                                .perform(post("/user/registration/submit-email").contentType("application/json")
                                                .content(objectMapper.writeValueAsString(request)).with(csrf()))
                                .andExpect(status().isBadRequest())
                                .andReturn();

                String expectedResponseBody = objectMapper.writeValueAsString(expectedOutcome);
                String actualResponseBody = mvcResult.getResponse().getContentAsString();

                assertThat(actualResponseBody).isEqualTo(expectedResponseBody);

        }

}
