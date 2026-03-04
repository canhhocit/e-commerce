package sv.project.e_commerce.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import sv.project.e_commerce.dto.request.*;
import sv.project.e_commerce.dto.response.ApiResponse;
import sv.project.e_commerce.dto.response.AuthenticationResponse;
import sv.project.e_commerce.dto.response.IntrospectResponse;
import sv.project.e_commerce.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Xác thực", description = "Các endpoint cho xác thực người dùng, đăng ký và quản lý token")
public class AuthenticationController {

    AuthenticationService authenticationService;

    @Operation(summary = "Đăng nhập", description = "Xác thực người dùng và trả về JWT token")
    @PostMapping("/login")
    public ApiResponse<AuthenticationResponse> authenticate(@RequestBody @Valid AuthenticationRequest request) {
        var result = authenticationService.authenticate(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }

    // Create user
    @Operation(summary = "Đăng ký", description = "Đăng ký tài khoản người dùng mới")
    @PostMapping("/register")
    public ApiResponse<String> register(@RequestBody @Valid UserCreateRequest request) {
        var result = authenticationService.register(request);
        return ApiResponse.<String>builder()
                .result(result)
                .build();
    }

    @Operation(summary = "Xác minh Email", description = "Xác minh email người dùng bằng token")
    @GetMapping("/verify")
    public ApiResponse<String> verifyEmail(@RequestParam("token") String token) {
        var result = authenticationService.verifyEmail(token);
        return ApiResponse.<String>builder()
                .result(result)
                .build();
    }

    @Operation(summary = "Kiểm tra Token", description = "Xác minh xem token có hợp lệ và còn hoạt động không")
    @PostMapping("/introspect")
    public ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request)
            throws JOSEException, ParseException {
        var result = authenticationService.introspect(request);
        return ApiResponse.<IntrospectResponse>builder()
                .result(result)
                .build();
    }

    @Operation(summary = "Đăng xuất", description = "Vô hiệu hóa token được cung cấp")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestBody LogoutRequest request) throws JOSEException, ParseException {
        authenticationService.logout(request);
        return ApiResponse.<Void>builder()
                .build();
    }

    @Operation(summary = "Làm mới Token", description = "Tạo một token mới bằng refresh token hợp lệ")
    @PostMapping("/refresh")
    public ApiResponse<AuthenticationResponse> refresh(@RequestBody RefreshRequest request)
            throws JOSEException, ParseException {
        var result = authenticationService.refreshToken(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }
}
