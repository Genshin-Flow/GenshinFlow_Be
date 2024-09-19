package com.next.genshinflow.application.user.service;


import com.next.genshinflow.domain.user.entity.MemberEntity;
import com.next.genshinflow.domain.user.repository.MemberRepository;
import com.next.genshinflow.exception.BusinessLogicException;
import com.next.genshinflow.exception.ExceptionCode;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

// 이 클래스는 Member 객체를 Spring Security가 이해할 수 있는 UserDetails 객체로 변환하여 인증 과정에서 사용함
@Component("userDetailsService")
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String email) {
        return memberRepository.findByEmail(email)
            .map(member -> createMember(member))
            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
    }

    private org.springframework.security.core.userdetails.User createMember(MemberEntity member) {
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(member.getRole());

        return new org.springframework.security.core.userdetails.User(
            member.getEmail(),
            member.getPassword(),
            Collections.singleton(grantedAuthority)
        );
    }
}
