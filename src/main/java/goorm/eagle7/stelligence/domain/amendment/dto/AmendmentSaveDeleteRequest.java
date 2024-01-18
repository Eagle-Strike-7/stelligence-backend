package goorm.eagle7.stelligence.domain.amendment.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class AmendmentSaveDeleteRequest { //수정안을 deletee로 생성할 때 dto
	private String title;
	private String description;
	private Long sectionId;
}
