package goorm.eagle7.stelligence.common.auth.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(staticName = "of") // 정적 팩토리 메서드
public class LoginRequest {

	private String nickname;

}
