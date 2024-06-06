package org.weare4saken.spring_jwt_auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.weare4saken.spring_jwt_auth.entity.Role;
import org.weare4saken.spring_jwt_auth.entity.User;
import org.weare4saken.spring_jwt_auth.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final RoleService roleService;
    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return this.userRepository.findAll();
    }

    public User getByUsername(String username) throws UsernameNotFoundException {
        return this.userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User %s not found".formatted(username)));
    }

    public boolean existsByUserName(String username) {
        return this.userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return this.userRepository.existsByEmail(email);
    }

    @Transactional
    public void createUser(User user) throws IllegalStateException {
        if (this.existsByUserName(user.getUsername())) {
            throw new IllegalStateException("Username already exists");
        }

        if (this.existsByEmail(user.getEmail())) {
            throw new IllegalStateException("Email already exists");
        }

        List<Role> dbRoles = this.roleService.getUserRoles(user);

        if (dbRoles.isEmpty()) {
            throw new IllegalStateException("Roles list empty");
        }

        user.setRoles(dbRoles);

        this.userRepository.save(user);
    }
}
