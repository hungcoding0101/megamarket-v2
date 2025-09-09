package hung.megamarketv2.common.otp.repositories;

import hung.megamarketv2.common.generic.results.Result;
import hung.megamarketv2.common.otp.ErrorCodes.OtpRepositoryErrorCodes;
import hung.megamarketv2.common.otp.Dto.OtpIdentity;

import java.util.List;
import java.util.Optional;

import hung.megamarketv2.common.generic.models.OtpRequest;

public interface OtpRepository {

    public Result<Optional<OtpRequest>, OtpRepositoryErrorCodes> findById(long id);

    public Result<Optional<OtpRequest>, OtpRepositoryErrorCodes> findByTextId(String textId);

    public Result<List<OtpRequest>, OtpRepositoryErrorCodes> findByIdentity(OtpIdentity otpIdentity);

    public Result<OtpRequest, OtpRepositoryErrorCodes> createOtpRequest(OtpRequest otpRequest);

}
