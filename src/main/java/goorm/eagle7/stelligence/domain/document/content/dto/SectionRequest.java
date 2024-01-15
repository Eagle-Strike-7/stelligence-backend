package goorm.eagle7.stelligence.domain.document.content.dto;

import goorm.eagle7.stelligence.domain.section.model.Heading;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Document 생성시 Section에 관한 정보를 담기 위한 요청 DTO 입니다.
 */
@AllArgsConstructor
@Getter
@Setter
public class SectionRequest {
	private Heading heading;
	private String title;
	private String content;
}
