package sv.project.e_commerce.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import sv.project.e_commerce.dto.request.AuthenticationRequest;
import sv.project.e_commerce.dto.request.UserCreateRequest;
import sv.project.e_commerce.dto.response.AuthenticationResponse;
import sv.project.e_commerce.exception.AppException;
import sv.project.e_commerce.exception.ErrorCode;
import sv.project.e_commerce.model.entity.User;
import sv.project.e_commerce.model.enums.Role;
import sv.project.e_commerce.repository.UserRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuthenticationService {

    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    EmailService emailService;

    @NonFinal
    @Value("${application.security.jwt.secret-key}")
    protected String SIGNER_KEY;

    @NonFinal
    @Value("${application.security.jwt.expiration}")
    protected long VALID_DURATION;
    // login
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        if (!user.isEnabled()) {
            throw new DisabledException("User not found");
        }

        var token = generateToken(user);

        return AuthenticationResponse.builder()
                .token(token)
                .build();
    }
    //register
    public String register(UserCreateRequest request) {
        Optional<User> userByUsername = userRepository.findByUsername(request.getUsername());
        if (userByUsername.isPresent()) {
            User user = userByUsername.get();
            String token = user.getVerificationToken();
            if (token != null && !token.isBlank() && user.getEmail().equals(request.getEmail())) {
                    throw new AppException(ErrorCode.ACCOUNT_NOT_VERIFIED);
            }else {
                throw new AppException(ErrorCode.USERNAME_EXISTED);
            }
        }
        if (userRepository.existsByEmailAndEnabledTrue(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }
        String token = UUID.randomUUID().toString();
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .address(request.getAddress())
                .role(Role.USER)
                .enabled(false)
                .verificationToken(token)
                .build();

        userRepository.save(user);
        emailService.sendVerificationEmail(user.getEmail(), token);
        return "Please check your email to verify your account";
    }
    //verify Email
    public String verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_TOKEN));

        user.setEnabled(true);
        user.setVerificationToken(null);
        userRepository.save(user);

        return "Email verified successfully";
    }
    // gen token
    private String generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("canhhocit")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", user.getRole().name())
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }
}
