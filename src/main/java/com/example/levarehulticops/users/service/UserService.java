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
}
