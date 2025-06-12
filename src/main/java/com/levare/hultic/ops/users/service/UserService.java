package com.levare.hultic.ops.users.service;

import com.levare.hultic.ops.users.entity.User;

import java.util.List;

/**
 * Service interface for managing User entities in GUI context.
 */
public interface UserService {

    User create(User user);

    User update(User user);

    void delete(Long id);

    User getById(Long id);

    List<User> getAll();
}
