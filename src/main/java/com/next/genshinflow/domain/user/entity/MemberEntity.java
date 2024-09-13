package com.next.genshinflow.domain.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.next.genshinflow.domain.BaseEntity;
import com.next.genshinflow.enumeration.AccountStatus;
import com.next.genshinflow.enumeration.Role;
import com.next.genshinflow.enumeration.converter.AccountStatusConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "member")
public class MemberEntity extends BaseEntity {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "uid", unique = true)
    private String uid;

    @Column(name = "name")
    private String name;

    @Column(name = "email", unique = true)
    private String email;

    @JsonIgnore
    @Column(name = "password", length = 20)
    private String password;

    @Column(name = "image")
    private String image;

    @Convert(converter = AccountStatusConverter.class)
    @Column(name = "status")
    private AccountStatus status;

    @Column(name = "role")
    private Role role;

    @ManyToMany
    @JoinTable(
        name = "member_authority",
        joinColumns = {@JoinColumn(name = "member_id", referencedColumnName = "member_id")},
        inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "authority_name")}
    )
    private Set<AuthorityEntity> authorities;
}
