package sv.project.e_commerce.controller;

import java.util.List;

import org.mapstruct.control.MappingControl.Use;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import sv.project.e_commerce.dto.request.UserCreateRequest;
import sv.project.e_commerce.dto.request.UserUpdateRequest;
import sv.project.e_commerce.dto.response.ApiResponse;
import sv.project.e_commerce.dto.response.UserResponse;
import sv.project.e_commerce.service.UserService;

@RestController
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/users")
public class UserController {
  UserService userService;

  @PostMapping
  ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreateRequest request) {
    log.info("CONTROLLER: create User");
    return userService.createUser(request);
  }

  @PutMapping("/{username}")
  ApiResponse<UserResponse> updateUser(@PathVariable String username, @RequestBody @Valid UserUpdateRequest request) {
    return userService.updateUser(username, request);
  }

  @GetMapping
  ApiResponse<List<UserResponse>> getUsers() {
    return userService.getUsers();
  }
  @GetMapping("/{username}")
  ApiResponse<UserResponse> getUser(@PathVariable String username) {
    return userService.getUserByUsername(username);
  }

  @DeleteMapping("/{username}")
  ApiResponse<String> deleteUser(@PathVariable String username) {
    return userService.deleteUser(username);
  }
}
