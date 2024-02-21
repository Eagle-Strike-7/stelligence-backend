package goorm.eagle7.stelligence.domain.member.dto;

import org.hibernate.validator.constraints.Length;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Schema(description = "수정할 닉네임 정보")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(staticName = "from")
public class MemberUpdateNicknameRequest {

	@NotBlank(message = "닉네임을 입력해주세요.")
	@Length(max = 15, message = "닉네임은 15자 이내로 입력해주세요.")
	@Schema(description = "수정할 닉네임", example = "은하세계")
	private String nickname;

}
