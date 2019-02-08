package com.masta.auth.membership.repository;

import com.masta.auth.membership.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Long> {

}
