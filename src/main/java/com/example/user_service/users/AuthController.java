package com.example.user_service.users;

import com.example.user_service.refresh.RefreshToken;
import com.example.user_service.refresh.RefreshTokenRequest;
import com.example.user_service.refresh.RefreshTokenService;
import com.example.user_service.security.JWTService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JWTService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthController(UserService userService, JWTService jwtService, RefreshTokenService refreshTokenService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signUpUser(@Valid @RequestBody SignUpRequest userRequest){
        var response = userService.registerUser(userRequest.name(),
                userRequest.email(), userRequest.password());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@Valid @RequestBody LoginRequest loginRequest)
    {
        var userResponse = userService.authenticateUser(loginRequest.email(), loginRequest.password());

        // Generate access token
        String accessToken = jwtService.generateToken(loginRequest.email());

        // Generate refresh token
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(
                userService.findUserByEmail(loginRequest.email()) // You'll need to add this method
        );

        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken.getToken(), userResponse));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        // Rotate the refresh token
        RefreshToken newRefreshToken = refreshTokenService.rotateRefreshTokenWithTheftDetection(request.refreshToken());

        // Generate new access token
        String newAccessToken = jwtService.generateToken(newRefreshToken.getUser().getEmail());

        // Return new access token
        return ResponseEntity.ok(new AuthResponse(
                newAccessToken,
                newRefreshToken.getToken(),
                new UserResponse(
                        newRefreshToken.getUser().getId(),
                        newRefreshToken.getUser().getName(),
                        newRefreshToken.getUser().getEmail(),
                        newRefreshToken.getUser().getRole()
                )
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody RefreshTokenRequest refreshTokenRequest)
    {
        refreshTokenService.deleteByToken(refreshTokenRequest.refreshToken());
        return ResponseEntity.noContent().build();
    }
}
