package com.masta.auth.membership.service;

import com.masta.auth.exception.ExceptionMessage;
import com.masta.auth.exception.exceptions.NoSuchDataException;
import com.masta.auth.membership.entity.GuestUser;
import com.masta.auth.membership.repository.GuestUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class GuestUserService {

    @Autowired
    GuestUserRepository guestUserRepository;

    @Transactional
    public GuestUser saveGuestUser() {
        GuestUser newUser = guestUserRepository.save(GuestUser.builder()
                        .authority("ROLE_USER")
                        .build());
        newUser.setGuestId(newUser.getNum().toString());
        GuestUser savedGuest = guestUserRepository.save(newUser);
        return savedGuest;
    }

    public GuestUser findGuestUser(String id) {
        return guestUserRepository.findByGuestId(id).orElseThrow(() -> new NoSuchDataException(ExceptionMessage.INVALID_USER_DATA));
    }

}
