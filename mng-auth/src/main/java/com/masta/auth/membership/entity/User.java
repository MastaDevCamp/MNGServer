package com.masta.auth.membership.entity;

import com.masta.auth.membership.dto.UserDetailsDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name="type")
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long num;

    private String authority; //need another setting!

    //later add created_at, updated_at.
    //private String nickname;

    public UserDetailsDTO touserDetailsDTO(){
        return UserDetailsDTO.builder().usernum(num.toString()).authority(authority).build();
    }

}
