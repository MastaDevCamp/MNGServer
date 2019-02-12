package com.masta.auth.membership.service;

import com.masta.auth.membership.entity.User;
import com.masta.auth.membership.repository.UserRepository;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public User saveUser(){
        User user = User.builder()
                .authority("ROLE_USER")
                .build();
        user= userRepository.save(user);
        return user;
    }

}
