package sv.project.e_commerce.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import sv.project.e_commerce.dto.request.AuthenticationRequest;
import sv.project.e_commerce.dto.request.UserCreateRequest;
import sv.project.e_commerce.dto.response.ApiResponse;
import sv.project.e_commerce.dto.response.AuthenticationResponse;
import sv.project.e_commerce.service.AuthenticationService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {

    AuthenticationService authenticationService;

    @PostMapping("/login")
    public ApiResponse<AuthenticationResponse> authenticate(@RequestBody @Valid AuthenticationRequest request) {
        var result = authenticationService.authenticate(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }
    //Create user
    @PostMapping("/register")
    public ApiResponse<String> register(@RequestBody @Valid UserCreateRequest request) {
        var result = authenticationService.register(request);
        return ApiResponse.<String>builder()
                .result(result)
                .build();
    }

    @GetMapping("/verify")
    public ApiResponse<String> verifyEmail(@RequestParam("token") String token) {
        var result = authenticationService.verifyEmail(token);
        return ApiResponse.<String>builder()
                .result(result)
                .build();
    }
}
