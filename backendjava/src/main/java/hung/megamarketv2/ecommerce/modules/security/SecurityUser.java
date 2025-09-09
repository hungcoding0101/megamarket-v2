package hung.megamarketv2.ecommerce.modules.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonDeserialize
@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@NoArgsConstructor
public class SecurityUser implements UserDetails {

    @JsonProperty("username")
    private String userName;

    private String password;

    private boolean isEnabled;

    private List<SimpleGrantedAuthority> authorities;

    public SecurityUser(String userName, String password, List<SimpleGrantedAuthority> authorities,
            boolean isEnabled) {
        this.userName = userName;
        this.password = password;
        this.authorities = authorities;
        this.isEnabled = isEnabled;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

}
