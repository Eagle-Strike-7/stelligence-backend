package goorm.eagle7.stelligence.domain.document.content.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.springframework.stereotype.Component;

import goorm.eagle7.stelligence.domain.document.content.dto.SectionRequest;
import goorm.eagle7.stelligence.domain.section.model.Heading;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 사용자로부터 글 생성 목적으로 들어온 문자열을
 * 섹션 단위로 파싱하는 클래스입니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DocumentParser {

	private static final String HEADING_PATTERN = "^#{1,6}.*";
	private static final Heading[] HEADING_VALUES = Heading.values();

	/**
	 * 사용자로부터 글 생성 목적으로 들어온 문자열을
	 * 섹션 단위로 파싱합니다.
	 * @param rawContent 사용자가 생성하고자 하는 글의 내용을 담은 필드로, 마크다운으로 작성되어있습니다.
	 * @return
	 */
	public List<SectionRequest> parse(String rawContent) {
		List<SectionRequest> results = new ArrayList<>();
		StringTokenizer tokenizer = new StringTokenizer(rawContent, "\n");

		StringBuilder sb = new StringBuilder();
		String[] splited;

		Heading tempHeading = null;
		String tempTitle = null;
		while (tokenizer.hasMoreTokens()) {
			String line = tokenizer.nextToken();

			if (line.matches(HEADING_PATTERN)) { // 제목 패턴을 만난다면 기존까지의 내용들을 하나의 섹션으로 만들어야 합니다.

				if (tempHeading != null) { // 첫번째 섹션의 제목을 만났을 때는 기존의 내용이 없으므로 제외합니다.
					results.add(new SectionRequest(tempHeading, tempTitle, sb.toString()));
				}

				// 빈칸을 기준으로 헤딩과 제목을 분리합니다.
				splited = line.split(" ");

				// 헤딩의 길이에 따라 헤딩의 값을 정합니다.
				tempHeading = HEADING_VALUES[splited[0].length() - 1];

				// 제목은 헤딩 이후의 문자열로 정합니다.
				tempTitle = splited[1].trim();

				// 기존의 내용을 초기화하고, 다음번 반복문부터 내용을 채워갑니다.
				sb.setLength(0);
			} else {
				sb.append(line).append("\n");
			}
		}

		// 마지막 섹션을 추가합니다.
		results.add(new SectionRequest(tempHeading, tempTitle, sb.toString()));
		return results;
	}
}
