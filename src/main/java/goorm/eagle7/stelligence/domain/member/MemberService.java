package goorm.eagle7.stelligence.domain.member;

import goorm.eagle7.stelligence.domain.member.dto.MemberBadgesResponse;
import goorm.eagle7.stelligence.domain.member.dto.MemberMiniProfileResponse;
import goorm.eagle7.stelligence.domain.member.dto.MemberMyPageResponse;
import goorm.eagle7.stelligence.domain.member.dto.MemberUpdateNicknameRequest;

public interface MemberService {

	MemberMyPageResponse getMyPageById(Long memberId);

	void delete(Long memberId);

	void updateNickname(Long memberId, MemberUpdateNicknameRequest memberUpdateNicknameRequest);

	MemberMiniProfileResponse getMiniProfileById(Long id);
}
