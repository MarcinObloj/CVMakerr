package com.oblojmarcin.cvmaker.repository;



import com.oblojmarcin.cvmaker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    Optional<User> findByVerificationToken(String token);
    Optional<User> findByResetPasswordToken(String resetPasswordToken);
    Optional<User> findByUsername(String username);
}
