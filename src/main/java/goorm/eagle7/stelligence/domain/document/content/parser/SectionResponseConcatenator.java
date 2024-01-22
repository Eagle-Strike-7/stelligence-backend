package goorm.eagle7.stelligence.domain.document.content.parser;

import java.util.List;

import goorm.eagle7.stelligence.domain.document.content.dto.SectionResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * SectionResponse로부터 섹션의 내용을 하나의 문자열로 합치는 클래스입니다.
 *
 * 사용자가 글 조회시 프론트엔드에서 섹션의 내용을 하나의 문자열로 합쳐서 보여주기 편리하게 만듦니다.
 * 물론 이때에도 Section 관련 정보는 함께 제공되어 사용자측에서 유연하게 사용할 수 있게 할 예정입니다.
 *
 */
@Slf4j
public class SectionResponseConcatenator {

	private SectionResponseConcatenator() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * SectionResponse로부터 섹션의 내용을 하나의 문자열로 합칩니다.
	 * @param sections
	 * @return
	 */
	public static String concat(List<SectionResponse> sections) {
		StringBuilder sb = new StringBuilder();
		sections.forEach(section -> sb.append(section.getFullContentString()));
		return sb.toString();
	}
}
