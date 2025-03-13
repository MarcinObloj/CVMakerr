package com.oblojmarcin.cvmaker.controller;

import com.oblojmarcin.cvmaker.dto.ChangePasswordRequest;
import com.oblojmarcin.cvmaker.entity.User;
import com.oblojmarcin.cvmaker.service.EmailService;
import com.oblojmarcin.cvmaker.service.UserService;
import com.oblojmarcin.cvmaker.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;


    @Autowired
    public UserController(UserService userService, EmailService emailService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.emailService = emailService;
        this.jwtUtil = jwtUtil;
    }


    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody User user) {
        Map<String, String> response = new HashMap<>();
        try {


            User registeredUser = userService.registerUser(user);
            response.put("message", "Użytkownik zarejestrował się pomyślnie. Token weryfikacyjny został wysłany na Twój adres e-mail.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            if (errorMessage.contains("Email already in use")) {
                response.put("message", "Email już został użyty do rejestracji.");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
            response.put("message", "Wystąpił problem podczas rejestracji: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginRequest) {
        try {
            String emailOrUsername = loginRequest.get("emailOrUsername");
            String password = loginRequest.get("password");
            User user = userService.loginUser(emailOrUsername, password);

            String token = jwtUtil.generateToken(user.getUsername());
            System.out.println(token);
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("userId", user.getUserId());
            response.put("username", user.getUsername());
            response.put("role", user.getRole().getName());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Login failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }


    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable int userId) {
        try {
            User user = userService.getUserById(userId);
            if (user != null) {
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error retrieving user: " + e.getMessage());
        }
    }


    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUserById(@PathVariable int userId) {
        boolean isDeleted = userService.deleteUserById(userId);
        if (isDeleted) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error retrieving users: " + e.getMessage());
        }
    }


    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        Map<String, String> response = new HashMap<>();

        try {
            boolean result = userService.resetPassword(email, "http://localhost:4200/reset");
            if (result) {
                response.put("message", "Na Twój adres e-mail została wysłana wiadomość z instrukcjami dotyczącymi resetowania hasła.");
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Nie znaleziono użytkownika o podanym adresie e-mail.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("message", "Wystąpił błąd: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }




    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        try {
            userService.changePassword(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok(Collections.singletonMap("message", "Hasło zostało zmienione pomyślnie."));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wystąpił błąd podczas zmiany hasła: " + e.getMessage());
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyUser(@RequestParam("code") String code) {
        try {
            userService.verifyUser(code);
            String htmlResponse = "<html>"
                    + "<head>"
                    + "<style>"
                    + "  body { font-family: Arial, sans-serif; color: #333333; font-size: 1.2rem; line-height: 1.6; padding: 20px; }"
                    + "  h2 { font-size: 2rem; color: #4CAF50; }"
                    + "  .btn-primary { color: #171b21; font-size: 1.6rem; padding: 1em 2em; border-radius: 1.6rem; background: #fec85b; color: #ffffff; text-align: center; font-weight: bold; margin: 2em auto; cursor: pointer; transition: background-color 0.3s; border: none; }"
                    + "  .btn-primary:hover { background-color: #d5a84c; }"
                    + "</style>"
                    + "</head>"
                    + "<body>"
                    + "<h2>Twoje konto zostało zweryfikowane!</h2>"
                    + "<button class='btn-primary' onclick=\"window.location.href='http://localhost:4200/login'\">Zaloguj się</button>"
                    + "</body>"
                    + "</html>";

            return ResponseEntity.ok().body(htmlResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Kod weryfikacyjny jest nieprawidłowy lub wygasł.");
        }
    }

}
