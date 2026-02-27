package sv.project.e_commerce.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import sv.project.e_commerce.dto.request.UserCreateRequest;
import sv.project.e_commerce.dto.request.UserUpdateRequest;
import sv.project.e_commerce.dto.response.UserResponse;
import sv.project.e_commerce.model.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "cart", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "verificationToken", ignore = true)
    User toUser(UserCreateRequest request);

    UserResponse toUserResponse(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "cart", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "verificationToken", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
