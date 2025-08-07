package com.eccolimp.cacamba_manager.security.service;

import com.eccolimp.cacamba_manager.security.model.User;
import com.eccolimp.cacamba_manager.security.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameOrEmail(login)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + login));

        return new CustomUserPrincipal(user);
    }

    /**
     * Implementação customizada do UserDetails
     */
    public static class CustomUserPrincipal implements UserDetails {
        private final User user;

        public CustomUserPrincipal(User user) {
            this.user = user;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
            );
        }

        @Override
        public String getPassword() {
            return user.getPassword();
        }

        @Override
        public String getUsername() {
            return user.getUsername();
        }

        @Override
        public boolean isAccountNonExpired() {
            return user.getAccountNonExpired();
        }

        @Override
        public boolean isAccountNonLocked() {
            return user.getAccountNonLocked();
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return user.getCredentialsNonExpired();
        }

        @Override
        public boolean isEnabled() {
            return user.getEnabled();
        }

        // Métodos para acessar dados do usuário
        public User getUser() {
            return user;
        }

        public String getNomeCompleto() {
            return user.getNomeCompleto();
        }

        public String getEmail() {
            return user.getEmail();
        }

        public User.Role getRole() {
            return user.getRole();
        }

        public Long getId() {
            return user.getId();
        }
    }
}