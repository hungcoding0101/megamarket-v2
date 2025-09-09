package hung.megamarketv2.integration.common.otp;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import hung.megamarketv2.common.generic.enums.OtpEnums.OtpType;
import hung.megamarketv2.common.generic.models.OtpRequest;
import hung.megamarketv2.common.otp.Dto.OtpIdentity;
import hung.megamarketv2.common.generic.results.Result;
import hung.megamarketv2.common.otp.repositories.OtpRepository;
import hung.megamarketv2.common.otp.ErrorCodes.OtpRepositoryErrorCodes;

@DataJpaTest
@Import(TestConfig.class)
@TestMethodOrder(OrderAnnotation.class)
class OtpRepositoryTest {

    private static final String REFERENCE_ID = "referenceId";
    private static final String REFERENCE_TYPE = "referenceType";
    private static final String HASHED_OTP = "referenceType";
    private static final String CREATE_OTP_TAG = "CREATE_OTP_TAG";
    private static final long OTP_LIFETIME = 120L;
    private static final long OTP_COOL_DOWN_TIME = 60L;

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PlatformTransactionManager transactionManager;

    private OtpRequest otpRequest;

    @BeforeEach
    void setUp(TestInfo testInfo) {
        if (testInfo.getTags().contains(CREATE_OTP_TAG)) {
            return;
        }

        LocalDateTime expiredAt = LocalDateTime.now().plusSeconds(OTP_LIFETIME);
        LocalDateTime canResendAt = LocalDateTime.now().plusSeconds(OTP_COOL_DOWN_TIME);

        OtpRequest request = new OtpRequest(OtpType.EMAIL, HASHED_OTP, REFERENCE_TYPE, REFERENCE_ID, canResendAt,
                expiredAt);

        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        this.otpRequest = transactionTemplate.execute(status -> {
            return otpRepository.createOtpRequest(request).value;
        });
    }

    @Test
    @Tag(CREATE_OTP_TAG)
    @Order(1)
    void testCreateOtpRequest() {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);

        Result<OtpRequest, OtpRepositoryErrorCodes> result = transactionTemplate.execute(status -> {
            return createOtpRequest();
        });

        assertNotNull(result);

        OtpRequest createdOtpRequest = transactionTemplate.execute(status -> {
            return entityManager.find(OtpRequest.class, result.value.getId());
        });

        assertNotNull(createdOtpRequest);
        assertNotNull(createdOtpRequest.getCreatedAt());
        assertNotNull(createdOtpRequest.getTextId());
    }

    @Test
    void testfindById() {
        Result<Optional<OtpRequest>, OtpRepositoryErrorCodes> result = otpRepository
                .findById(otpRequest.getId());

        assertTrue(result.isSuccessful);

        Optional<OtpRequest> maybeOtpRequest = result.value;
        assertTrue(maybeOtpRequest.isPresent());

        OtpRequest foundOtpRequest = maybeOtpRequest.get();

        assertEquals(otpRequest, foundOtpRequest);
    }

    @Test
    void testfindByIdButNotFoundShouldReturnEmpty() {
        Result<Optional<OtpRequest>, OtpRepositoryErrorCodes> result = otpRepository
                .findById(otpRequest.getId() + 1);

        assertTrue(result.isSuccessful);

        Optional<OtpRequest> maybeOtpRequest = result.value;
        assertFalse(maybeOtpRequest.isPresent());

    }

    @Test
    void testfindByTextId() {
        Result<Optional<OtpRequest>, OtpRepositoryErrorCodes> result = otpRepository
                .findByTextId(otpRequest.getTextId());

        assertTrue(result.isSuccessful);

        Optional<OtpRequest> maybeOtpRequest = result.value;
        assertTrue(maybeOtpRequest.isPresent());

        OtpRequest foundOtpRequest = maybeOtpRequest.get();
        assertEquals(otpRequest, foundOtpRequest);
    }

    @Test
    void testfindByIdentity() {
        Result<List<OtpRequest>, OtpRepositoryErrorCodes> result = otpRepository.findByIdentity(
                new OtpIdentity(otpRequest.getType(), otpRequest.getReferenceType(), otpRequest.getReferenceId()));

        assertTrue(result.isSuccessful);

        List<OtpRequest> foundOtpRequests = result.value;
        assertEquals(1, foundOtpRequests.size());

        OtpRequest foundOtpRequest = foundOtpRequests.get(0);

        assertEquals(otpRequest, foundOtpRequest);
    }

    private Result<OtpRequest, OtpRepositoryErrorCodes> createOtpRequest() {
        LocalDateTime expiredAt = LocalDateTime.now().plusSeconds(OTP_LIFETIME);
        LocalDateTime canResendAt = LocalDateTime.now().plusSeconds(OTP_COOL_DOWN_TIME);

        OtpRequest request = new OtpRequest(OtpType.EMAIL, HASHED_OTP, REFERENCE_TYPE, REFERENCE_ID, canResendAt,
                expiredAt);

        return otpRepository.createOtpRequest(request);

    }

}
