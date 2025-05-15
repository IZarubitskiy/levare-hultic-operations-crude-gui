package com.example.levarehulticops.users.mapper;

import com.example.levarehulticops.users.dto.UserDto;
import com.example.levarehulticops.users.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    /**
     * Преобразует одну сущность в DTO
     */
    public UserDto toDto(User e) {
        return new UserDto(
                e.getId(),
                e.getName(),
                e.getPosition()
        );
    }
}
