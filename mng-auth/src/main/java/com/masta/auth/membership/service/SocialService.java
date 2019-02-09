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
        SocialUser user = new SocialUser();
        Optional<SocialUser> saveUser = socialUserRepository.findBySocialId(socialUserForm.getSocial_id());
        if (!saveUser.isPresent()) {
            user = socialUserForm.toEntity();
            user.setAuthority("ROLE_USER");
            user = socialUserRepository.save(user);
        }
        else user=saveUser.get();
        return user;
    }
}
