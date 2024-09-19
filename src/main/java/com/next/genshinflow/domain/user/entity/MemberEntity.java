package com.next.genshinflow.domain.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.next.genshinflow.domain.BaseEntity;
import com.next.genshinflow.enumeration.AccountStatus;
import com.next.genshinflow.enumeration.Role;
import com.next.genshinflow.enumeration.converter.AccountStatusConverter;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@Table(name = "member")
@AllArgsConstructor
@NoArgsConstructor
public class MemberEntity extends BaseEntity {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "uid", unique = true)
    private long uid;

    @Column(name = "name")
    private String name;

    @Column(name = "email", unique = true)
    private String email;

    @JsonIgnore
    @Column(name = "password")
    private String password;

    @Column(name = "image")
    private String image;

    @Convert(converter = AccountStatusConverter.class)
    @Column(name = "status")
    private AccountStatus status;

    private String role;
}
