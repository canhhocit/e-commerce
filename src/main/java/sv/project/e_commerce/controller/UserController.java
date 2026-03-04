package sv.project.e_commerce.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import sv.project.e_commerce.dto.request.UserUpdateRequest;
import sv.project.e_commerce.dto.response.ApiResponse;
import sv.project.e_commerce.dto.response.UserResponse;
import sv.project.e_commerce.service.UserService;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/users")
@Tag(name = "Người dùng", description = "Các endpoint cho hồ sơ và quản lý người dùng")
@SecurityRequirement(name = "bearerAuth")
public class UserController {
    UserService userService;

    @Operation(summary = "Lấy thông tin của tôi", description = "Lấy thông tin hồ sơ của người dùng hiện đang đăng nhập")
    @GetMapping("/my-info")
    public ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyInfo())
                .build();
    }

    @Operation(summary = "Lấy tất cả người dùng", description = "Lấy danh sách tất cả các người dùng đang hoạt động (Chỉ ADMIN)")
    @GetMapping
    public ApiResponse<List<UserResponse>> getUsers() {
        return ApiResponse.<List<UserResponse>>builder()
                .result(userService.getUsers())
                .build();
    }

    @Operation(summary = "Lấy người dùng theo Username", description = "Lấy thông tin cụ thể của người dùng theo username")
    @GetMapping("/{username}")
    public ApiResponse<UserResponse> getUser(@PathVariable String username) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getUserByUsername(username))
                .build();
    }

    @Operation(summary = "Cập nhật người dùng", description = "Cập nhật thông tin hồ sơ của một người dùng cụ thể")
    @PutMapping("/{username}")
    public ApiResponse<UserResponse> updateUser(@PathVariable String username,
            @RequestBody @Valid UserUpdateRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateUser(username, request))
                .build();
    }

    @Operation(summary = "Xóa người dùng", description = "Vô hiệu hóa tài khoản người dùng theo username")
    @DeleteMapping("/{username}")
    public ApiResponse<String> deleteUser(@PathVariable String username) {
        return ApiResponse.<String>builder()
                .result(userService.deleteUser(username))
                .build();
    }
}