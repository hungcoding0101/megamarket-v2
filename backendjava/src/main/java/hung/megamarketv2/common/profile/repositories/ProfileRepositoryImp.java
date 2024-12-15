package hung.megamarketv2.common.profile.repositories;

import hung.megamarketv2.common.generic.models.Profile;
import hung.megamarketv2.common.generic.models.QProfile;
import hung.megamarketv2.common.generic.results.Result;
import hung.megamarketv2.common.profile.ErrorCodes.ProfileRepositoryErrorCodes;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import com.querydsl.jpa.impl.JPAQueryFactory;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.jpa.JPQLTemplates;
import jakarta.annotation.PostConstruct;

@Repository
@Transactional(propagation = Propagation.REQUIRED)
public class ProfileRepositoryImp implements ProfileRepository {

    @PersistenceContext
    private EntityManager entityManager;
    private JPAQueryFactory queryFactory;

    @PostConstruct
    public void init() {
        queryFactory = new JPAQueryFactory(JPQLTemplates.DEFAULT, entityManager);
    }

    @Override
    public Result<Profile, ProfileRepositoryErrorCodes> createProfile(Profile profile) {
        try {
            entityManager.persist(profile);
            return Result.ofValue(profile);
        } catch (EntityExistsException e) {
            return Result.ofError(ProfileRepositoryErrorCodes.PROFILE_ALREADY_EXISTS);
        }
    }

    @Override
    public Result<Profile, ProfileRepositoryErrorCodes> findById(Long id) {
        Profile profile = entityManager.find(Profile.class, id);

        if (profile == null) {
            return Result.ofError(ProfileRepositoryErrorCodes.PROFILE_NOT_FOUND);
        }

        return Result.ofValue(profile);
    }

    @Override
    public Result<Profile, ProfileRepositoryErrorCodes> findByTextId(String textId) {
        QProfile qProfile = QProfile.profile;
        Profile profile = queryFactory
                .selectFrom(qProfile)
                .where(qProfile.textId.eq(textId))
                .fetchOne();

        if (profile == null) {
            return Result.ofError(ProfileRepositoryErrorCodes.PROFILE_NOT_FOUND);
        }

        return Result.ofValue(profile);

    }

}
