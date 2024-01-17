package goorm.eagle7.stelligence.common.login.dto;

import static lombok.AccessLevel.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(staticName = "of") // 정적 팩토리 메서드
public class LoginRequest {
	// TODO 닉네임 중복 체크 이후에 대한 요청인 게 드러나도록 이름 수정하기
	private String nickname;

}
