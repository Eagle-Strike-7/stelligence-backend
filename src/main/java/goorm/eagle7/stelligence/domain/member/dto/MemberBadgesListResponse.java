package goorm.eagle7.stelligence.domain.member.dto;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberBadgesListResponse {

	private List<MemberBadgesResponse> badges ;

	public static MemberBadgesListResponse from(List<MemberBadgesResponse> badges) {
		return new MemberBadgesListResponse(badges);
	}

}
