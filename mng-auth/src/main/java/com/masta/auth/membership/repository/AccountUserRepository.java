package com.masta.auth.membership.repository;

import com.masta.auth.membership.entity.AccountUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountUserRepository extends JpaRepository<AccountUser, Long> {
    AccountUser findByUsername(String username);
}
