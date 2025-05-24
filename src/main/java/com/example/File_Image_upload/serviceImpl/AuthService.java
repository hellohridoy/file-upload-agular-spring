package com.example.File_Image_upload.serviceImpl;

import com.example.File_Image_upload.dto.*;
import com.example.File_Image_upload.entity.PasswordResetToken;
import com.example.File_Image_upload.entity.RefreshToken;
import com.example.File_Image_upload.entity.User;
import com.example.File_Image_upload.repository.PasswordResetTokenRepository;
import com.example.File_Image_upload.repository.UserRepository;
import com.example.File_Image_upload.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final EmailService emailService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager,
                       RefreshTokenService refreshTokenService,
                       EmailService emailService,
                       PasswordResetTokenRepository passwordResetTokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.refreshTokenService = refreshTokenService;
        this.emailService = emailService;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists!");
        }

        User user = new User(
            request.getEmail(),
            passwordEncoder.encode(request.getPassword()),
            request.getFirstName(),
            request.getLastName()
        );

        user = userRepository.save(user);

        String jwtToken = jwtService.generateToken(user);

        // Delete any existing refresh token for this user before creating new one
        refreshTokenService.deleteByUserId(user.getId());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        return new AuthResponse(
            jwtToken,
            refreshToken.getToken(),
            jwtService.getExpirationTime(),
            user.getEmail(),
            user.getFirstName(),
            user.getLastName()
        );
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );

        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String jwtToken = jwtService.generateToken(user);

        // Delete any existing refresh token for this user before creating new one
        refreshTokenService.deleteByUserId(user.getId());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        return new AuthResponse(
            jwtToken,
            refreshToken.getToken(),
            jwtService.getExpirationTime(),
            user.getEmail(),
            user.getFirstName(),
            user.getLastName()
        );
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
            .map(refreshTokenService::verifyExpiration)
            .map(RefreshToken::getUser)
            .map(user -> {
                String token = jwtService.generateToken(user);
                return new AuthResponse(
                    token,
                    requestRefreshToken,
                    jwtService.getExpirationTime(),
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName()
                );
            })
            .orElseThrow(() -> new RuntimeException(
                "Refresh token is not in database!"));
    }

    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + request.getEmail()));

        // Delete existing password reset tokens for this user
        passwordResetTokenRepository.deleteByUser(user);

        // Create new password reset token
        String token = UUID.randomUUID().toString();
        PasswordResetToken passwordResetToken = new PasswordResetToken(token, user);
        passwordResetTokenRepository.save(passwordResetToken);

        // Send email
        emailService.sendPasswordResetEmail(user.getEmail(), token);
    }

    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(request.getToken())
            .orElseThrow(() -> new RuntimeException("Invalid password reset token"));

        if (passwordResetToken.isExpired()) {
            throw new RuntimeException("Password reset token has expired");
        }

        if (passwordResetToken.isUsed()) {
            throw new RuntimeException("Password reset token has already been used");
        }

        User user = passwordResetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Mark token as used
        passwordResetToken.setUsed(true);
        passwordResetTokenRepository.save(passwordResetToken);

        // Also invalidate any existing refresh tokens for security
        refreshTokenService.deleteByUserId(user.getId());
    }

    public void logout(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        refreshTokenService.deleteByUserId(user.getId());
    }
}
