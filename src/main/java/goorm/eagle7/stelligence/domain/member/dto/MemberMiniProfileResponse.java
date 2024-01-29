package goorm.eagle7.stelligence.domain.member.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(staticName = "of")
public class MemberMiniProfileResponse {
	private String nickname;
	private String profileImgUrl;
}
