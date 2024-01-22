package goorm.eagle7.stelligence.config.mockdata;

import goorm.eagle7.stelligence.domain.amendment.model.Amendment;
import goorm.eagle7.stelligence.domain.amendment.model.AmendmentType;
import goorm.eagle7.stelligence.domain.contribute.model.Contribute;
import goorm.eagle7.stelligence.domain.document.content.model.Document;
import goorm.eagle7.stelligence.domain.member.model.Member;
import goorm.eagle7.stelligence.domain.section.model.Heading;
import goorm.eagle7.stelligence.domain.section.model.Section;

public class TestFixtureGenerator {

	private TestFixtureGenerator() {
	}

	public static Member member(String nickname) {
		return Member.of("name", nickname, "email", "imageUrl", "socialId");
	}

	public static Document document(String title, Member author) {
		return Document.createDocument(title, author);
	}

	public static Section section(Document document, Long id, Long revision, String title, int order) {
		return Section.createSection(document, id, revision, Heading.H1, title, "content", order);
	}

	public static Amendment amendment(Contribute contribute, AmendmentType type, Section section,
		Heading heading, String title, String content, Integer order) {
		Amendment amendment;

		// AmendmentType에 따라 적절한 팩토리 메서드 호출
		if (type == AmendmentType.CREATE) {
			amendment = Amendment.forCreate(section, heading, title, content, order);
		} else if (type == AmendmentType.UPDATE) {
			amendment = Amendment.forUpdate(section, heading, title, content);
		} else {
			amendment = Amendment.forDelete(section);
		}

		amendment.setContribute(contribute);
		return amendment;
	}

	public static Contribute contribute() {
		return Contribute.createContribute();
	}
}
