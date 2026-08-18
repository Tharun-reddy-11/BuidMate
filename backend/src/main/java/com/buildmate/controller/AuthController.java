package com.buildmate.controller;

import com.buildmate.dto.ApiDtos.*;
import com.buildmate.entity.User;
import com.buildmate.repository.UserRepository;
import com.buildmate.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController @RequestMapping("/api/auth")
public class AuthController {
  private final UserRepository users; private final PasswordEncoder encoder; private final JwtService jwt;
  public AuthController(UserRepository u,PasswordEncoder e,JwtService j){users=u;encoder=e;jwt=j;}
  @PostMapping("/login") public ResponseEntity<?> login(@Valid @RequestBody LoginRequest r){return users.findByEmailIgnoreCase(r.email()).filter(u->u.getRole()==User.Role.ADMIN&&encoder.matches(r.password(),u.getPasswordHash())).<ResponseEntity<?>>map(u->ResponseEntity.ok(response(u))).orElseGet(()->ResponseEntity.status(401).body(Map.of("message","Invalid admin credentials")));}
  private AuthResponse response(User u){return new AuthResponse(jwt.create(u),u.getFullName(),u.getEmail(),u.getRole().name());}
}
