package com.masta.auth.membership.entity;

import com.masta.auth.membership.dto.UserDetailsDTO;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

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
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
    //private String nickname;

//    public User() {
//        final LocalDateTime now = LocalDateTime.now();
//        createdAt = now;
//        updatedAt = now;
//    }

    public UserDetailsDTO touserDetailsDTO(){
        return UserDetailsDTO.builder()
                .usernum(num.toString())
                .authority(authority)
                .build();
    }

}
