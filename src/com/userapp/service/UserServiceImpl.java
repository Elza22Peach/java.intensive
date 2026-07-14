package com.userapp.service;

import com.userapp.dao.UserDAO;
import com.userapp.entity.User;
import com.userapp.dao.UserDAOImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;

public class UserServiceImpl implements UserService {
    private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);
    private final UserDAO userDAO;

    public UserServiceImpl() {
        this.userDAO = new UserDAOImpl();
    }

    @Override
    public User createUser(String name, String email, Integer age) {
        validateUserData(name, email, age);

        if (userDAO.existsByEmail(email)) {
            throw new RuntimeException("User with email " + email + " already exists");
        }

        User user = new User(name, email, age);
        return userDAO.create(user);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid user id");
        }
        return userDAO.findById(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    @Override
    public User updateUser(Long id, String name, String email, Integer age) {
        validateUserData(name, email, age);

        User existingUser = userDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("User with id " + id + " not found"));

        // Check if email is being changed and is already taken
        if (!existingUser.getEmail().equals(email) && userDAO.existsByEmail(email)) {
            throw new RuntimeException("Email " + email + " is already taken");
        }

        existingUser.setName(name);
        existingUser.setEmail(email);
        existingUser.setAge(age);

        return userDAO.update(existingUser);
    }

    @Override
    public void deleteUser(Long id) {
        userDAO.delete(id);
    }

    private void validateUserData(String name, String email, Integer age) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (age == null || age < 0 || age > 150) {
            throw new IllegalArgumentException("Age must be between 0 and 150");
        }
    }
}
