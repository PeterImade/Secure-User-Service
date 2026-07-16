package com.example.user_service.users;

import com.example.user_service.exceptions.EmailAlreadyExistsException;
import com.example.user_service.exceptions.InvalidCredentialsException;
import com.example.user_service.exceptions.UserNotFoundException;
import jakarta.persistence.Id;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse registerUser(String name, String email, String password) {

        var userExists = userRepository.existsByEmail(email);

        if(userExists)
        {
            // Create custom exceptions later and handle them globally
            throw new EmailAlreadyExistsException("User already exists");
        }

        var hashedPassword = passwordEncoder.encode(password);

        User user = User.builder()
                .name(name)
                .email(email)
                .password(hashedPassword)
                .role(UserRole.USER)
                .build();

        User savedUser = userRepository.save(user);

        return new UserResponse(savedUser.getId(), savedUser.getName(), savedUser.getEmail(), savedUser.getRole());
    }

    public UserResponse authenticateUser(String email, String rawPassword){

        User storedUser = userRepository.findByEmail(email).orElseThrow(() -> new InvalidCredentialsException("Invalid email or password."));

        boolean passwordMatches = passwordEncoder.matches(rawPassword, storedUser.getPassword());

        if (!passwordMatches)
        {
            throw new InvalidCredentialsException("Invalid email or password.");
        }

        return new UserResponse(storedUser.getId(), storedUser.getName(), storedUser.getEmail(), storedUser.getRole());
    }

    public User findUserByEmail(String email)
    {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        return user;
    }

    public List<UserResponse> getUsers(int page, int size)
    {
        Page<User> pageList = userRepository.findAll(PageRequest.of(page, size));

        List<UserResponse> usersList = pageList.getContent().stream().map(user -> new UserResponse(user.getId(), user.getName(),
                user.getEmail(), user.getRole())).collect(Collectors.toList());

        return usersList;
    }

    public UserResponse getUserById(Long id)
    {
        var user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found."));

        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }

    public UserResponse updateUser(Long id, UpdateUserRequest request)
    {
        var user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found."));

        if (request.name() != null && !request.name().isBlank())
        {
            user.setName(request.name());
        }

        if (request.email() != null && !request.email().isBlank())
        {
            if (!request.email().equals(user.getEmail()) && userRepository.existsByEmail(request.email()))
            {
                throw new EmailAlreadyExistsException("Email already in use: " + request.email());
            }

            user.setEmail(request.email());
        }

        User updatedUser = userRepository.save(user);

        return new UserResponse(updatedUser.getId(), updatedUser.getName(), updatedUser.getEmail(), updatedUser.getRole());
    }

    public void deleteUser(Long id)
    {
        if (!userRepository.existsById(id)){
            throw new UserNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
}
