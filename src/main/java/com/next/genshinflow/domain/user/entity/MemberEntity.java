package com.next.genshinflow.domain.user.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.next.genshinflow.domain.BaseEntity;
import com.next.genshinflow.enumeration.Role;
import com.next.genshinflow.enumeration.converter.RoleConverter;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
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

    @Column(name = "level")
    private int level;

    @Column(name = "world_level")
    private int worldLevel;

    @Column(name = "towerLevel")
    private String towerLevel;

    @Convert(converter = RoleConverter.class)
    @Column(name = "role")
    private Role role;

    @Column(name = "oauth_user")
    private Boolean oAuthUser;

    @Column(name = "discipline_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime disciplineDate;

    @ElementCollection
    @CollectionTable(name = "member_disciplinary_history", joinColumns = @JoinColumn(name = "member_id"))
    private List<Discipline> disciplinaryHistory = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "member_warnings", joinColumns = @JoinColumn(name = "member_id"))
    private List<Warning> warningHistory = new ArrayList<>();
}
