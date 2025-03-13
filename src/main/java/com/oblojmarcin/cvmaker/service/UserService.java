package com.oblojmarcin.cvmaker.service;

import com.oblojmarcin.cvmaker.entity.Roles;
import com.oblojmarcin.cvmaker.entity.User;
import com.oblojmarcin.cvmaker.repository.RoleRepository;
import com.oblojmarcin.cvmaker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService  {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Autowired
    public UserService(UserRepository userRepository, EmailService emailService, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository= roleRepository;
    }


    @Transactional
    public User registerUser(User user) throws Exception {
        System.out.println("Start of registerUser");

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new Exception("Ten email został już użyty do rejestracji.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setVerificationToken(generateVerificationToken());
        user.setTokenExpiration(LocalDateTime.now().plusDays(1));
        user.setEmailVerified(false);

        // Fetch the role from the database
        Roles role = roleRepository.findById(1)
                .orElseThrow(() -> new Exception("Role not found"));

        user.setRole(role);
        user.setCreatedAt(LocalDateTime.now());

        User registeredUser = userRepository.save(user);

        System.out.println("User saved, sending email");

        String baseVerifyUrl = "http://localhost:8080/api/users/verify";
        String verificationUrl = baseVerifyUrl + "?code=" + registeredUser.getVerificationToken();
        emailService.sendVerificationEmail(registeredUser, verificationUrl);

        System.out.println("Email sent");

        return registeredUser;
    }


    private String generateVerificationToken() {
        return UUID.randomUUID().toString();
    }



    public boolean verifyUser(String token) {
        Optional<User> optionalUser = userRepository.findByVerificationToken(token);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getTokenExpiration() != null && user.getTokenExpiration().isAfter(LocalDateTime.now())) {
                user.setEmailVerified(true);
                user.setVerificationToken(null);
                user.setTokenExpiration(null);
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }


    public boolean deleteUserById(int userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
            return true;
        }
        return false;
    }



    public User loginUser(String emailOrUsername, String password) throws Exception {
        Optional<User> optionalUser = userRepository.findByEmail(emailOrUsername)
                .or(() -> userRepository.findByUsername(emailOrUsername));

        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException("Użytkownik nie znaleziony.");
        }

        User user = optionalUser.get();
        if (user.getEmailVerified() && passwordEncoder.matches(password, user.getPassword())) {
            return user;
        } else {
            throw new IllegalArgumentException("Nieprawidłowy email albo hasło");
        }
    }



    public boolean resetPassword(String email, String siteURL) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            String resetToken = UUID.randomUUID().toString();
            user.setResetPasswordToken(resetToken);
            userRepository.save(user);

            String resetUrl = siteURL + "?token=" + resetToken;
            emailService.sendPasswordResetEmail(user, resetUrl);
            return true;
        } else {
            return false;
        }
    }



    public void changePassword(String token, String newPassword) throws Exception {
        Optional<User> optionalUser = userRepository.findByResetPasswordToken(token);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setResetPasswordToken(null); // Usuń token po zmianie hasła
            userRepository.save(user);
        } else {
            throw new Exception("Nieprawidłowy token.");
        }
    }


    private String generateResetToken() {
        return UUID.randomUUID().toString();
    }



    public User getUserById(int userId) {
        return userRepository.findById(userId).orElse(null);
    }


    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (!optionalUser.isPresent()) {
            optionalUser = userRepository.findByEmail(username);
            if (!optionalUser.isPresent()) {
                throw new UsernameNotFoundException("Użytkownik nie znaleziony.");
            }
        }

        User user = optionalUser.get();
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }
}