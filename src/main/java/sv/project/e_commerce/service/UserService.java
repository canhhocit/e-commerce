package sv.project.e_commerce.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import sv.project.e_commerce.dto.request.UserCreateRequest;
import sv.project.e_commerce.dto.request.UserUpdateRequest;
import sv.project.e_commerce.dto.response.ApiResponse;
import sv.project.e_commerce.dto.response.UserResponse;
import sv.project.e_commerce.exception.AppException;
import sv.project.e_commerce.exception.ErrorCode;
import sv.project.e_commerce.mapper.UserMapper;
import sv.project.e_commerce.model.entity.User;
import sv.project.e_commerce.model.enums.Role;
import sv.project.e_commerce.repository.UserRepository;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
        UserRepository userRepository;
        UserMapper userMapper;

        PasswordEncoder passwordEncoder;

        // C
        public ApiResponse<UserResponse> createUser(UserCreateRequest request) {
                if (userRepository.existsByUsername(request.getUsername())) {
                        throw new AppException(ErrorCode.USER_EXISTED);
                }

                if (userRepository.existsByEmail(request.getEmail())) {
                        throw new AppException(ErrorCode.EMAIL_EXISTED);
                }
                User user = userMapper.toUser(request);
                user.setPassword(passwordEncoder.encode(request.getPassword()));
                user.setRole(Role.CUSTOMER);
                return ApiResponse.<UserResponse>builder()
                                .result(userMapper.toUserResponse(userRepository.save(user)))
                                .build();
        }

        // R
        public ApiResponse<UserResponse> getUserByUsername(String username) {
                User user = userRepository.findByUsername(username)
                                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
                return ApiResponse.<UserResponse>builder()
                                .result(userMapper.toUserResponse(user))
                                .build();
        }

        public ApiResponse<List<UserResponse>> getUsers() {
                List<UserResponse> users = userRepository.findAll().stream().map(userMapper::toUserResponse).toList();
                return ApiResponse.<List<UserResponse>>builder().result(users).build();
        }

        // U
        public ApiResponse<UserResponse> updateUser(String username, UserUpdateRequest request) {
                Optional<User> userByEmail = userRepository.findByEmail(request.getEmail());

                if (userByEmail.isPresent() &&
                                !userByEmail.get().getUsername().equals(username)) {
                        throw new AppException(ErrorCode.EMAIL_EXISTED);
                }
                User user = userRepository.findByUsername(username)
                                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
                userMapper.updateUser(user, request);
                user.setPassword(passwordEncoder.encode(request.getPassword()));
                return ApiResponse.<UserResponse>builder()
                                .result(userMapper.toUserResponse(userRepository.save(user)))
                                .build();
        }

        // D
        public ApiResponse<String> deleteUser(String username) {

                User user = userRepository.findByUsername(username)
                                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

                userRepository.delete(user);

                return ApiResponse.<String>builder()
                                .result("deleted")
                                .build();
        }
}
