package goorm.eagle7.stelligence.domain.document.content.dto;

import goorm.eagle7.stelligence.domain.section.model.Heading;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Document 생성시 Section에 관한 정보를 담기 위한 DTO 입니다.
 * 사용자로부터 받은 전체 문서 내용은 DocumentParser를 통해 해당 형태로 변환하여 이후 로직을 수행합니다.
 */
@AllArgsConstructor
@Getter
@Setter
public class SectionRequest {
	private Heading heading;
	private String title;
	private String content;
}
