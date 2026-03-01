package sv.project.e_commerce.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import sv.project.e_commerce.dto.request.UserUpdateRequest;
import sv.project.e_commerce.dto.response.UserResponse;
import sv.project.e_commerce.exception.AppException;
import sv.project.e_commerce.exception.ErrorCode;
import sv.project.e_commerce.mapper.UserMapper;
import sv.project.e_commerce.model.entity.User;
import sv.project.e_commerce.repository.UserRepository;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;

    // findOne
    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsernameAndEnabledTrue(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return userMapper.toUserResponse(user);
    }

    // findAll
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getUsers() {
        return userRepository.findAllByEnabledTrue().stream()
                .map(userMapper::toUserResponse).toList();
    }

    // Update
    public UserResponse updateUser(String username, UserUpdateRequest request) {
        Optional<User> userByEmail = userRepository.findByEmailAndEnabledTrue(request.getEmail());
        if (userByEmail.isPresent() && !userByEmail.get().getUsername().equals(username)) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (!user.isEnabled()) throw new AppException(ErrorCode.USER_NOT_EXISTED);
        userMapper.updateUser(user, request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        return userMapper.toUserResponse(userRepository.save(user));
    }

    // Delete
    public String deleteUser(String username) {
        User user = userRepository.findByUsernameAndEnabledTrue(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        user.setEnabled(false);
        user.setVerificationToken("");
        userRepository.save(user);
        return "deleted";
    }
}