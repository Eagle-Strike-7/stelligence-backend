package goorm.eagle7.stelligence.domain.section.model;

import lombok.Getter;

/**
 * 문단의 제목 수준을 나타내는 열거형 타입입니다.
 */
@Getter
public enum Heading {
	H1("h1"),
	H2("h2"),
	H3("h3");

	private final String tag;

	private static final String TAG_FORMAT = "<%s>%s</%s>";

	/**
	 * 제목 태그를 포함한 전체 제목 태그를 반환합니다.
	 * @param title 제목
	 * @return 제목 태그를 포함한 전체 제목 태그
	 */
	public String getFullHeadingTag(String title) {
		return String.format(TAG_FORMAT, tag, title, tag);
	}

	Heading(String tag) {
		this.tag = tag;
	}
}
