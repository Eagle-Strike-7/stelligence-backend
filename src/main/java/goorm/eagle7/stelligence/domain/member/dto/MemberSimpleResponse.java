package goorm.eagle7.stelligence.domain.member.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberSimpleResponse {

	private Long memberId;
	private String nickname;
	private String imageUrl;
}
