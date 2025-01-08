package com.next.genshinflow.application.user.service;

import com.next.genshinflow.application.user.mapper.MemberMapper;
import com.next.genshinflow.domain.user.entity.MemberEntity;
import com.next.genshinflow.domain.user.repository.MemberRepository;
import com.next.genshinflow.infrastructure.enkaApi.EnkaService;
import com.next.genshinflow.infrastructure.enkaApi.UserInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserProfileService {
    private final EnkaService enkaService;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberEntity buildMemberEntity(long uid, String email, String password, UserInfoResponse apiResponse, boolean oauth) {
        String profileImgPath = enkaService.getIconPathForProfilePicture(apiResponse.getPlayerInfo().getProfilePicture().getId());
        String tower = apiResponse.getPlayerInfo().getTowerFloorIndex() + "-" + apiResponse.getPlayerInfo().getTowerLevelIndex();

        MemberEntity member = MemberMapper.toMember(uid, email, apiResponse, profileImgPath, tower, oauth);
        if (password != null) member.setPassword(passwordEncoder.encode(password));
        return member;
    }

    // 받아온 api를 조회해 변경된 유저 정보가 있으면 업데이트
    public void updateMemberIfPlayerInfoChanged(MemberEntity member) {
        UserInfoResponse.PlayerInfo playerInfo = enkaService.getUserInfoFromApi(member.getUid()).getPlayerInfo();
        if (playerInfo == null) return;
        boolean isUpdated = false;

        if (!member.getName().equals(playerInfo.getNickname())) {
            member.setName(playerInfo.getNickname());
            isUpdated = true;
        }
        if (member.getLevel() != playerInfo.getLevel()) {
            member.setLevel(playerInfo.getLevel());
            isUpdated = true;
        }
        if (member.getWorldLevel() != playerInfo.getWorldLevel()) {
            member.setWorldLevel(playerInfo.getWorldLevel());
            isUpdated = true;
        }

        String tower = playerInfo.getTowerFloorIndex() + "-" + playerInfo.getTowerLevelIndex();
        if (!member.getTowerLevel().equals(tower)) {
            member.setTowerLevel(tower);
            isUpdated = true;
        }

        if (isUpdated) {
            memberRepository.save(member);
        }
    }
}
