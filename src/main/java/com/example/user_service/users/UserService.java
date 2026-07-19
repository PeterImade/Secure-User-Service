package com.example.user_service.users;

import com.example.user_service.exceptions.EmailAlreadyExistsException;
import com.example.user_service.exceptions.InvalidCredentialsException;
import com.example.user_service.exceptions.UserNotFoundException;
import com.example.user_service.refresh.RefreshToken;
import com.example.user_service.refresh.RefreshTokenService;
import com.example.user_service.security.JWTService;
import jakarta.persistence.Id;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final RefreshTokenService refreshTokenService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JWTService jwtService, RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    public UserResponse registerUser(String name, String email, String password) {

        var userExists = userRepository.existsByEmail(email);

        if(userExists)
        {
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

    public AuthResponse authenticateUser(String email, String rawPassword){

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, rawPassword));

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        User storedUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFoundException("Invalid email or password"));

        var userResponse = new UserResponse(storedUser.getId(), storedUser.getName(), storedUser.getEmail(), storedUser.getRole());

        // Generate access token
        String accessToken = jwtService.generateToken(email);

        // Generate refresh token
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(storedUser);

        return new AuthResponse(accessToken, refreshToken.getToken(), userResponse);
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
