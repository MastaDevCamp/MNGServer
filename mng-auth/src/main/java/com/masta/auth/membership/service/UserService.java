package com.masta.auth.membership.service;

import com.masta.auth.membership.entity.User;
import com.masta.auth.membership.repository.AccountUserRepository;
import com.masta.auth.membership.repository.GuestUserRepository;
import com.masta.auth.membership.repository.SocialUserRepository;
import com.masta.auth.membership.repository.UserRepository;
import com.masta.core.response.ResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public List<List> getUserList() {
        List<List> userList = new ArrayList<>();

        if(!socialUserRepository.findAll().isEmpty())
            userList.add(socialUserRepository.findAll());
        if(!accountUserRepository.findAll().isEmpty())
            userList.add(accountUserRepository.findAll());
        if(!guestUserRepository.findAll().isEmpty())
            userList.add(guestUserRepository.findAll());

        List<User> userList1 = userRepository.findAll();
        log.info(userList1.toString());

        return userList;
    }

    public User getUser(Long id) {
        Optional<User> user = userRepository.findById(id);

        return user.get();
    }

    public String updateUserRole(final Long id, final String role) {
        User user = userRepository.getOne(id);

        if (user == null) {
            return ResponseMessage.NOT_FOUND_USER;
        }
        if (user.getAuthority().equals(role)) {
            return ResponseMessage.ALREADY_GOT_ROLE+ " : "+ role;
        }

        user.setAuthority(role);
        userRepository.save(user);

        return ResponseMessage.CHANGE_USER_ROLE;
    }
}
