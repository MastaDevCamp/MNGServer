package com.masta.auth.membership.service;

import com.masta.auth.exception.ExceptionMessage;
import com.masta.auth.exception.exceptions.NoSuchDataException;
import com.masta.auth.membership.entity.AccountUser;
import com.masta.auth.membership.entity.User;
import com.masta.auth.membership.repository.AccountUserRepository;
import com.masta.auth.membership.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Slf4j
@Service
public class AccountUserService implements UserDetailsService {
    final AccountUserRepository accountUserRepository;
    final UserRepository userRepository;
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AccountUserService(AccountUserRepository accountUserRepository, UserRepository userRepository) {
        this.accountUserRepository = accountUserRepository;
        this.userRepository = userRepository;
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
    public User createUser(AccountUser accountUser) {
        String pw = accountUser.getPassword();
        String encodedPw = new BCryptPasswordEncoder().encode(pw);
        accountUser.setPassword(encodedPw);
        User user = accountUserRepository.save(accountUser);
        return user;
    }

    @Transactional
    public void switchUser(AccountUser accountUser, long num) {
        User user = userRepository.findById(num).orElseThrow(()-> new NoSuchDataException(ExceptionMessage.INVALID_USER_DATA));
        userRepository.delete(user);
        String pw = accountUser.getPassword();
        String encodedPw = new BCryptPasswordEncoder().encode(pw);
        accountUser.setPassword(encodedPw);
        AccountUser newaccountUser = accountUserRepository.save(accountUser);
        accountUserRepository.updateUserNum(num, newaccountUser.getNum());
        userRepository.updateUserNum(num, newaccountUser.getNum());
    }

    public PasswordEncoder passwordEncoder() {
        return this.passwordEncoder;
    }


}
