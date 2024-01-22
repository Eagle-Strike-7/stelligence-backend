package goorm.eagle7.stelligence.domain.section.model;

import lombok.Getter;

/**
 * 문단의 제목 수준을 나타내는 열거형 타입입니다.
 */
@Getter
public enum Heading {
	H1("#"),
	H2("##"),
	H3("###"),
	H4("####"),
	H5("#####"),
	H6("######");

	private String symbol;

	Heading(String symbol) {
		this.symbol = symbol;
	}
}
