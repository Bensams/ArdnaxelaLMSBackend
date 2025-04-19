package com.ardnaxela.library_management_system.Mapper;

import com.ardnaxela.library_management_system.User.User;
import com.ardnaxela.library_management_system.User.UserDTO;

public class UserMapper {

    // Method to convert User entity to UserDTO
    public static UserDTO toUserDTO(User user) {
        if (user == null) {
            return null;
        }
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setPassword(user.getPassword());
        userDTO.setRole(user.getRole());
        userDTO.setMemberDTO(user.getMember() != null ? MemberMapper.toDTO(user.getMember()) : null);
        return userDTO;
    }

    // Method to convert UserDTO to User entity
    public static User toUser(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        }
        User user = new User();
        user.setId(userDTO.getId());
        user.setUsername(userDTO.getUsername());
        user.setPassword(userDTO.getPassword());
        user.setRole(userDTO.getRole());
        user.setMember(userDTO.getMemberDTO() != null ? MemberMapper.toEntity(userDTO.getMemberDTO()) : null);

        return user;
    }
}
