package goorm.eagle7.stelligence.domain.member.dto;

import goorm.eagle7.stelligence.domain.badge.model.Badge;
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
	private String badgeEventCategory; // Badge 획득 조건카테고리 (ex. 글, 댓글 등)
	private String badgeDescription; // Badge 설명
	private String badgeImgUrl; // Badge 이미지 url


	public static MemberBadgesResponse from(Badge badge) {
		return new MemberBadgesResponse(
			badge.name(),
			badge.getTitle(),
			badge.getEventCategory().getLabel(),
			badge.getImgUrl(),
			badge.getDescription()
		);
	}
}
