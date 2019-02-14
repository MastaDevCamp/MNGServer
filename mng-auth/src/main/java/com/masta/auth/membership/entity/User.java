package com.masta.auth.membership.entity;

import com.masta.auth.membership.dto.UserDetailsDTO;
import lombok.*;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name="type")
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class User {

    @Id
    @GeneratedValue
    private Long num;

    private String authority; //need another setting!

    //later add created_at, updated_at.
    //private String nickname;

    public UserDetailsDTO touserDetailsDTO(){
        return UserDetailsDTO.builder()
                .usernum(num.toString())
                .authority(authority)
                .build();
    }

}
