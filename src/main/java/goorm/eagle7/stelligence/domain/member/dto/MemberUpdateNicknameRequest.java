package goorm.eagle7.stelligence.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Schema(description = "수정할 닉네임 정보")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(staticName = "of")
public class MemberUpdateNicknameRequest {

	@Schema(description = "수정할 닉네임", example = "은하세계")
	private String nickname;

}
