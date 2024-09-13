package com.next.genshinflow.domain.user.entity;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column
    private String uid;

    @Column
    private String name;

    @Column
    private String email;

    @Column
    private String password;

    @Column
    private String image;

    @Convert(converter = AccountStatusConverter.class)
    @Column
    private AccountStatus status;

    @Column
    private Role role;

    @ManyToMany
    @JoinTable(
        name = "member_authority",
        joinColumns = {@JoinColumn(name = "member_id", referencedColumnName = "member_id")},
        inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "authority_name")}
    )
    private Set<AuthorityEntity> authorities;
}
