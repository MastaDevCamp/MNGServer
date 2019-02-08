package com.masta.auth.membership.service;


import com.masta.auth.membership.dto.SocialUserForm;
import com.masta.auth.membership.entity.SocialUser;
import com.masta.auth.membership.repository.SocialUserRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class SocialService {

    private final SocialUserRepository socialUserRepository;

    public SocialService(SocialUserRepository socialUserRepository) {
        this.socialUserRepository = socialUserRepository;
    }

    @Transactional
    public SocialUser getOrSave(SocialUserForm socialUserForm) {
        SocialUser newUser = new SocialUser();
        Optional<SocialUser> saveUser = socialUserRepository.findBySocialId(socialUserForm.getSocial_id());
        if (!saveUser.isPresent()) {
            newUser = socialUserForm.toEntity();
            //newUser.addRole(new UserRole("ROLE_USER"));
            newUser = socialUserRepository.save(newUser);
        }
        return newUser;
    }
//    public UsernamePasswordAuthenticationToken doAuthentication(SocialUserForm socialUserForm){
//        userService.getOrSave(socialUserForm);
//        return setAuthenticationToken(socialUserForm);
//    }
//
//    private UsernamePasswordAuthenticationToken setAuthenticationToken(Object user){
//        return new UsernamePasswordAuthenticationToken(user,null,getAuthorities("ROLE_USER"));
//    }
//
//    private Collection<? extends GrantedAuthority> getAuthorities(String role){
//        List<GrantedAuthority> authorities = new ArrayList<>();
//        authorities.add(new SimpleGrantedAuthority(role));
//        return authorities;
//    }
}
