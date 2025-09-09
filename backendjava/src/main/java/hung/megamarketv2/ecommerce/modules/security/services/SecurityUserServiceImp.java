package hung.megamarketv2.ecommerce.modules.security.services;

import java.util.Collections;
import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hung.megamarketv2.common.generic.constants.SecurityConstants;
import hung.megamarketv2.common.generic.models.Password;
import hung.megamarketv2.common.generic.models.User;
import hung.megamarketv2.common.generic.results.Result;
import hung.megamarketv2.common.password.ErrorCodes.PasswordRepositoryErrorCodes;
import hung.megamarketv2.common.password.repositories.PasswordRepository;
import hung.megamarketv2.common.user.ErrorCodes.UserRepositoryErrorCodes;
import hung.megamarketv2.common.user.repositories.UserRepository;
import hung.megamarketv2.ecommerce.modules.security.SecurityUser;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class SecurityUserServiceImp implements SecurityUserService {

    private final UserRepository userRepository;

    private final PasswordRepository passwordRepository;

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Result<User, UserRepositoryErrorCodes> userResult = userRepository.findByEmail(email);

        if (!userResult.isSuccessful) {
            throw new UsernameNotFoundException("No user found for email: " + email);
        }

        User user = userResult.value;

        Result<Password, PasswordRepositoryErrorCodes> passwordResult = passwordRepository
                .findEnabledPasswordByUser(user);

        if (!passwordResult.isSuccessful) {
            throw new UsernameNotFoundException("No password found for user: " + user.getId());
        }

        Password password = passwordResult.value;

        String roleString = SecurityConstants.ROLE_PREFIX + user.getRole().toString();

        List<SimpleGrantedAuthority> authorities = Collections
                .singletonList(new SimpleGrantedAuthority(roleString));
        String stringId = Long.toString(user.getId());

        return new SecurityUser(stringId, password.getValue(), authorities, user.isEnabled());
    }

}