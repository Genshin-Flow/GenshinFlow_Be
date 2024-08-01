package com.next.genshinflow.domain.entity;

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
import org.hibernate.annotations.Comment;

@Getter
@Entity
@Table(name = "member")
public class MemberEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column
    private String name;

    @Column
    private String email;

    @Column
    @Comment("writer 가 없을 경우 사용하는 password 해시값")
    private String password;

    @Column
    @Comment("password hash 생성 시 사용한 salt 값")
    private String salt;

    @Column
    private String image;

    @Convert(converter = AccountStatusConverter.class)
    @Column
    private AccountStatus status;

    @Column
    private Role role;
}
