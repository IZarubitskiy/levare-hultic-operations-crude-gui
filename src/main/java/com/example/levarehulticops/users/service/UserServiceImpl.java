// src/main/java/com/example/levarehulticops/service/impl/UserServiceImpl.java
package com.example.levarehulticops.users.service;

import com.example.levarehulticops.users.dto.UserDto;
import com.example.levarehulticops.users.entity.User;
import com.example.levarehulticops.users.mapper.UserMapper;
import com.example.levarehulticops.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository repo;
    private final UserMapper userMapper;

    @Override
    public User create(User user) {
        return repo.save(user);
    }

    @Override
    public User update(User user) {
        if (!repo.existsById(user.getId())) {
            throw new EntityNotFoundException("User not found: " + user.getId());
        }
        return repo.save(user);
    }

    @Override
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new EntityNotFoundException("User not found: " + id);
        }
        repo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public User getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDto> getAll(Pageable pageable) {
        return repo.findAll(pageable).map(userMapper::toDto);
    }
}
