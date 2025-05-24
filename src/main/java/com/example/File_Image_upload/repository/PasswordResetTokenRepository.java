package com.example.File_Image_upload.repository;

import com.example.File_Image_upload.entity.PasswordResetToken;
import com.example.File_Image_upload.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    Optional<PasswordResetToken> findByUser(User user);
    void deleteByExpiryDateLessThan(LocalDateTime now);
    void deleteByUser(User user);
}
