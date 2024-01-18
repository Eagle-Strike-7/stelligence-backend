package goorm.eagle7.stelligence.common.login.dto;

import static lombok.AccessLevel.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


// test를 위한 닉네임 RequestDTO
@Getter
@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(staticName = "from") // 정적 팩토리 메서드
public class DevLoginRequest {
	private String nickname;
}
