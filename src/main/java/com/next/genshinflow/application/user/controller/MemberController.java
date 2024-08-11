package com.next.genshinflow.application.user.controller;

import com.next.genshinflow.application.user.mapper.MemberMapper;
import com.next.genshinflow.application.user.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final MemberMapper memberMapper;
}
