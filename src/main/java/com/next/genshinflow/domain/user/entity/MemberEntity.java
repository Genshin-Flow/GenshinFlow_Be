package com.next.genshinflow.domain.user.entity;

import com.next.genshinflow.domain.BaseEntity;
import com.next.genshinflow.enumeration.AccountStatus;
import com.next.genshinflow.enumeration.Role;
import com.next.genshinflow.enumeration.converter.AccountStatusConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

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
}
