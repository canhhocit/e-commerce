package sv.project.e_commerce.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
public class UserController {
    UserService userService;

    @GetMapping
    public ApiResponse<List<UserResponse>> getUsers() {
        return ApiResponse.<List<UserResponse>>builder()
                .result(userService.getUsers())
                .build();
    }

    @GetMapping("/{username}")
    public ApiResponse<UserResponse> getUser(@PathVariable String username) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getUserByUsername(username))
                .build();
    }

    @PutMapping("/{username}")
    public ApiResponse<UserResponse> updateUser(@PathVariable String username, @RequestBody @Valid UserUpdateRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateUser(username, request))
                .build();
    }

    @DeleteMapping("/{username}")
    public ApiResponse<String> deleteUser(@PathVariable String username) {
        return ApiResponse.<String>builder()
                .result(userService.deleteUser(username))
                .build();
    }
}