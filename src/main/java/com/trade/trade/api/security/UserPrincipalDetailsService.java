package com.trade.trade.api.security;

import com.trade.trade.domain.exceptions.ResourceNotFoundException;
import com.trade.trade.domain.models.User;
import com.trade.trade.api.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserPrincipalDetailsService implements UserDetailsService {
    private UserRepository userRepository;

    public UserPrincipalDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserPrincipal loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(User.class, username));
        return new UserPrincipal(user);
    }
}
