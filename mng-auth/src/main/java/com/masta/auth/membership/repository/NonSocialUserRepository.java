package com.masta.auth.membership.repository;

import com.masta.auth.membership.entity.NonSocialUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NonSocialUserRepository  extends JpaRepository<NonSocialUser, Long> {
    NonSocialUser findByUsername(String username);
}
