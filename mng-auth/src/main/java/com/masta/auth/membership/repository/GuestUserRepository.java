package com.masta.auth.membership.repository;

import com.masta.auth.membership.entity.GuestUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GuestUserRepository extends JpaRepository<GuestUser, Long> {
    Optional<GuestUser> findByGuestId(String  guestId);
}
