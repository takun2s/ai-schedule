package com.scheduler.service;

import com.scheduler.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public interface UserService {

    User findByUsername(String username) ;

    List<User> findAll() ;

    User findById(Long id);

    @Transactional
    public void register(User user) ;

    @Transactional
    public void update(User user) ;

    @Transactional
    public void delete(Long id) ;

    public boolean validatePassword(User user, String password) ;

    @Transactional
    public void updateLoginTime(User user) ;

    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) ;

    public User getCurrentUser() ;
    String generateToken(User user);

    @Transactional
    public void setEnabled(Long userId, boolean enabled) ;
}