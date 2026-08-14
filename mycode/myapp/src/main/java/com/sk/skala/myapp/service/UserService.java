package com.sk.skala.myapp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.sk.skala.myapp.domain.User;
import com.sk.skala.myapp.repository.UserRepository;

import jakarta.validation.Valid;

@Service
@Validated
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Transactional
    public User createUser(@Valid User user) {
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }

    @Transactional
    public Optional<User> updateUser(long id, @Valid User updateUser) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return Optional.empty();
        }
        User user = optionalUser.get();
        user.setName(updateUser.getName());
        user.setEmail(updateUser.getEmail());
        User savedUser = userRepository.save(user);
        return Optional.of(savedUser);
    }
}
