package com.example.codereview.service;

import com.example.codereview.dto.auth.LoginRequest;
import com.example.codereview.dto.auth.LoginResponse;
import com.example.codereview.dto.auth.RegisterRequest;
import com.example.codereview.dto.user.UserResponse;
import com.example.codereview.entity.GithubAccount;
import com.example.codereview.entity.User;
import com.example.codereview.enums.RoleType;
import com.example.codereview.exception.ConflictException;
import com.example.codereview.exception.ResourceNotFoundException;
import com.example.codereview.mapper.UserMapper;
import com.example.codereview.repository.GithubAccountRepository;
import com.example.codereview.repository.UserRepository;
import com.example.codereview.security.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final GithubAccountRepository githubAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserMapper userMapper;
    private final ActivityService activityService;

    public AuthService(UserRepository userRepository,
                       GithubAccountRepository githubAccountRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtUtils jwtUtils,
                       UserMapper userMapper,
                       ActivityService activityService) {
        this.userRepository = userRepository;
        this.githubAccountRepository = githubAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userMapper = userMapper;
        this.activityService = activityService;
    }

    @Transactional
    public LoginResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("An account with email " + request.getEmail() + " already exists");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole() != null ? request.getRole() : RoleType.DEVELOPER);

        User savedUser = userRepository.save(user);

        activityService.record(savedUser, "USER_REGISTERED", "Account created successfully with role " + savedUser.getRole());

        String token = jwtUtils.generateToken(savedUser.getEmail(), savedUser.getRole().name());
        UserResponse userResponse = userMapper.toResponse(savedUser, null);

        return new LoginResponse(token, userResponse);
    }

    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        GithubAccount githubAccount = githubAccountRepository.findByUserId(user.getId()).orElse(null);

        String token = jwtUtils.generateToken(user.getEmail(), user.getRole().name());
        UserResponse userResponse = userMapper.toResponse(user, githubAccount);

        activityService.record(user, "USER_LOGIN", "User logged in to platform");

        return new LoginResponse(token, userResponse);
    }

    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        GithubAccount githubAccount = githubAccountRepository.findByUserId(user.getId()).orElse(null);
        return userMapper.toResponse(user, githubAccount);
    }

    public User getUserEntityByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }
}
