package com.masta.auth.membership.service;

import com.masta.auth.membership.entity.AccountUser;
import com.masta.auth.membership.repository.AccountUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class AccountUserService implements UserDetailsService {
    final AccountUserRepository accountUserRepository;
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AccountUserService(AccountUserRepository accountUserRepository) {
        this.accountUserRepository = accountUserRepository;
    }

    public AccountUser getUser(String username){
        return accountUserRepository.findByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AccountUser accountUser = accountUserRepository.findByUsername(username);
        accountUser.setAuthorities(accountUser.getAuthorities());
        return accountUser;
    }

    @Transactional
    public void createUser(AccountUser accountUser) {
        String pw = accountUser.getPassword();
        String encodedPw = new BCryptPasswordEncoder().encode(pw);
        accountUser.setPassword(encodedPw);
        accountUserRepository.save(accountUser);
    }

    public PasswordEncoder passwordEncoder() {
        return this.passwordEncoder;
    }


}
