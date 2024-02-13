package goorm.eagle7.stelligence.common.dev.dto;

import goorm.eagle7.stelligence.domain.member.model.Role;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(staticName = "of")
public class DevLoginTokensWithIdAndRoleResponse {

	private String accessToken;
	private String refreshToken;
	private Long memberId;
	private Role role;

}
