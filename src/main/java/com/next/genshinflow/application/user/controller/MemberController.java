package com.next.genshinflow.application.user.controller;

import com.next.genshinflow.application.user.dto.SignUpRequest;
import com.next.genshinflow.application.user.mapper.MemberMapper;
import com.next.genshinflow.application.user.service.MemberService;
import com.next.genshinflow.domain.user.entity.MemberEntity;
import com.next.genshinflow.domain.utils.UriCreator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.net.URI;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final MemberMapper memberMapper;

    @PostMapping("/signup")
    public ResponseEntity signUpMember(@Valid @RequestBody SignUpRequest request) {
        MemberEntity member = memberMapper.postToMember(request);

        MemberEntity createdMember = memberService.createMember(member);
        URI location = UriCreator.createUri("/member", createdMember.getId());

        return ResponseEntity.created(location).build();
    }

    public ResponseEntity loginGoogle() {
        return null;
    }

    public ResponseEntity loginNaver() {
        return null;
    }

    @PostMapping()
    public ResponseEntity logout() {
        return null;
    }

    @GetMapping()
    public ResponseEntity getMember() {
        return null;
    }

    @GetMapping
    public ResponseEntity getMembers() {
        return null;
    }

    @PatchMapping()
    public ResponseEntity setProfileImg() {
        return null;
    }

    @PatchMapping()
    public ResponseEntity patchMember() {
        return null;
    }

    @PatchMapping()
    public ResponseEntity patchStatusActive() {
        return null;
    }

    @PatchMapping()
    public ResponseEntity patchStatusDelete() {
        return null;
    }

    @DeleteMapping()
    public ResponseEntity deleteMember() {
        return null;
    }
}
