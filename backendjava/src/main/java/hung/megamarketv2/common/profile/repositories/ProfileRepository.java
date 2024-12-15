package hung.megamarketv2.common.profile.repositories;

import hung.megamarketv2.common.generic.models.Profile;
import hung.megamarketv2.common.generic.results.Result;
import hung.megamarketv2.common.profile.ErrorCodes.ProfileRepositoryErrorCodes;

public interface ProfileRepository {
    public Result<Profile, ProfileRepositoryErrorCodes> findById(Long id);

    public Result<Profile, ProfileRepositoryErrorCodes> findByTextId(String textId);

    public Result<Profile, ProfileRepositoryErrorCodes> createProfile(Profile profile);
}
