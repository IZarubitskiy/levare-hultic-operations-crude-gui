package com.example.levarehulticops.users.service;

import com.example.levarehulticops.users.dto.UserDto;
import com.example.levarehulticops.users.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    User create(User user);

    User update(User user);

    void delete(Long id);

    User getById(Long id);

    Page<UserDto> getAll(Pageable pageable);

    /**
     * Get a user by their username, or throw if not found.
     *
     * @param username the username to look up
     * @return the User entity
     * @throws EntityNotFoundException if no user exists with that username
     */
    User getUserByUsername(String username);
}
