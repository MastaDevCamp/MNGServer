package com.masta.auth.membership.repository;

import com.masta.auth.membership.entity.AccountUser;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface AccountUserRepository extends JpaRepository<AccountUser, Long> {
    AccountUser findByUsername(String username);

    @Modifying
    @Query(value = "UPDATE AccountUser a set a.num = ?1 where a.num= ?2 ")
    void updateUserNum(Long originnum, Long newnum);
}
