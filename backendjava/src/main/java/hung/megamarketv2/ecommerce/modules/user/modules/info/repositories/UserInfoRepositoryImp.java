package hung.megamarketv2.ecommerce.modules.user.modules.info.repositories;

// import java.util.List;
// import java.util.Map;

// import org.springframework.stereotype.Repository;
// import org.springframework.transaction.annotation.Propagation;
// import org.springframework.transaction.annotation.Transactional;

// import com.querydsl.core.group.GroupBy;
// import com.querydsl.jpa.JPQLTemplates;
// import com.querydsl.jpa.impl.JPAQueryFactory;

// import hung.megamarketv2.common.generic.models.Profile;
// import hung.megamarketv2.common.generic.models.QProfile;
// import hung.megamarketv2.common.generic.models.QUser;
// import hung.megamarketv2.common.generic.models.User;
// import hung.megamarketv2.common.generic.results.Result;
// import hung.megamarketv2.ecommerce.modules.user.modules.info.Dto.UserInfo;
// import
// hung.megamarketv2.ecommerce.modules.user.modules.info.ErrorCodes.UserInfoRepositoryErrorCodes;
// import jakarta.annotation.PostConstruct;
// import jakarta.persistence.EntityManager;
// import jakarta.persistence.PersistenceContext;

// @Repository
// @Transactional(propagation = Propagation.REQUIRED)
// public class UserInfoRepositoryImp implements UserInfoRepository {
// @PersistenceContext
// private EntityManager entityManager;

// private JPAQueryFactory queryFactory;

// @PostConstruct
// public void init() {
// queryFactory = new JPAQueryFactory(JPQLTemplates.DEFAULT, entityManager);
// }

// @Override
// public Result<UserInfo, UserInfoRepositoryErrorCodes> findUserInfo(long
// userId) {
// QUser qUser = QUser.user;
// QProfile qProfile = QProfile.profile;

// Map<User, List<Profile>> result =
// queryFactory.selectFrom(qUser).leftJoin(qProfile).on(qUser.id.eq(qProfile.id))
// .where(qUser.id.eq(userId)).transform(GroupBy.groupBy(qUser).as(GroupBy.list(qProfile)));

// if (result.isEmpty()) {
// return Result.ofError(UserInfoRepositoryErrorCodes.USER_NOT_FOUND);
// }

// User user = result.keySet().iterator().next();
// List<Profile> profiles = result.get(user);

// return Result.ofValue(new UserInfo());
// }
// }
