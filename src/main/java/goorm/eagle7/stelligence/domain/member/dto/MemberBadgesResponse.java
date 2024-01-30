package goorm.eagle7.stelligence.domain.member.dto;

import goorm.eagle7.stelligence.domain.member.model.Badge;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberBadgesResponse {
	private String badgeType; // Badge enum name(type)
	private String badgeTitle; // Badge 사용자에게 제공하는 이름

	public static MemberBadgesResponse from(Badge badge) {
		return new MemberBadgesResponse(badge.name(), badge.getTitle());
	}
}
