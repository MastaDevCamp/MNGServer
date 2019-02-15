package com.masta.auth.membership.repository;

import com.masta.auth.membership.entity.SocialUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SocialUserRepository  extends JpaRepository<SocialUser, Long> {
    Optional<SocialUser> findBySocialId(String id);

}
