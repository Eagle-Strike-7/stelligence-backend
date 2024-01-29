package goorm.eagle7.stelligence.domain.document.content.parser;

import java.util.List;

import goorm.eagle7.stelligence.domain.document.content.dto.SectionRequest;

public interface DocumentParser {
	List<SectionRequest> parse(String rawContent);
}
