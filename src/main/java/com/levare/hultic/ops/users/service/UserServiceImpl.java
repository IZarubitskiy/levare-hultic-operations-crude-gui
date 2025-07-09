package com.levare.hultic.ops.users.service;

import com.levare.hultic.ops.users.dao.UserDao;
import com.levare.hultic.ops.users.entity.User;

import java.util.List;

/**
 * Default implementation of UserService for GUI applications using plain DAO.
 */
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public User create(User user) {
        userDao.insert(user);
        return user;
    }

    @Override
    public User update(User user) {
        User existing = getById(user.getId());
        existing.setName(user.getName());
        existing.setPosition(user.getPosition());
        userDao.update(existing);
        return existing;
    }

    @Override
    public void delete(Long id) {
        getById(id); // throw if not found
        userDao.deleteById(id);
    }

    @Override
    public User getById(Long id) {
        return userDao.findById(id);
    }

    @Override
    public List<User> getAll() {
        return userDao.findAll();
    }
}
