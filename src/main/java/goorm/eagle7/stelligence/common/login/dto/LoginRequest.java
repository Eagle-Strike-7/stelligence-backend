package goorm.eagle7.stelligence.common.login.dto;

import static lombok.AccessLevel.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(staticName = "of") // 정적 팩토리 메서드
public class LoginRequest {

	private String nickname;

}
