package com.masta.auth.membership.service;

import com.masta.auth.exception.ExceptionMessage;
import com.masta.auth.exception.exceptions.NoSuchDataException;
import com.masta.auth.membership.entity.User;
import com.masta.auth.membership.repository.AccountUserRepository;
import com.masta.auth.membership.repository.GuestUserRepository;
import com.masta.auth.membership.repository.SocialUserRepository;
import com.masta.auth.membership.repository.UserRepository;
import com.masta.core.response.ResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    SocialUserRepository socialUserRepository;

    @Autowired
    AccountUserRepository accountUserRepository;

    @Autowired
    GuestUserRepository guestUserRepository;

    public List<User> getUserList() {
        List<User> userList =  userRepository.findAll();
        return userList;
    }

    public User getUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(()-> new NoSuchDataException(ExceptionMessage.INVALID_USER_DATA));
        return user;
    }

    public String updateUserRole(final Long id, final String role) {
        User user = userRepository.getOne(id);
        LocalDateTime now = LocalDateTime.now();

        if (user == null) {
            return ResponseMessage.NOT_FOUND_USER;
        }
        if (user.getAuthority().equals(role)) {
            return ResponseMessage.ALREADY_GOT_ROLE+ " : "+ role;
        }

        user.setAuthority(role);
        user.setUpdatedAt(now);
        userRepository.save(user);

        return ResponseMessage.CHANGE_USER_ROLE;
    }

    public void DeleteUser(long usernum){
        User user = getUser(usernum);
        userRepository.delete(user);
    }
}
