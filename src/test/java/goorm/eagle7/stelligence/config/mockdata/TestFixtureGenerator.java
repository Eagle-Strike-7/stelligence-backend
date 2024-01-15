package goorm.eagle7.stelligence.config.mockdata;

import goorm.eagle7.stelligence.domain.amendment.model.Amendment;
import goorm.eagle7.stelligence.domain.amendment.model.AmendmentType;
import goorm.eagle7.stelligence.domain.contribute.model.Contribute;
import goorm.eagle7.stelligence.domain.document.model.Document;
import goorm.eagle7.stelligence.domain.member.model.Member;
import goorm.eagle7.stelligence.domain.section.model.Heading;
import goorm.eagle7.stelligence.domain.section.model.Section;

public class TestFixtureGenerator {

	private TestFixtureGenerator() {
	}

	public static Member member(String nickname) {
		return new Member("name", nickname, "email", "imageUrl", "refreshToken", "socialId");
	}

	public static Document document(String title) {
		return Document.createDocument(title);
	}

	public static Section section(Document document, Long id, Long revision, String title, int order) {
		return Section.createSection(document, id, revision, Heading.H1, title, "content", order);
	}

	public static Amendment amendment(Member member, Section section, String amendmentTitle, AmendmentType type,
		Heading heading, String title, String content, Contribute contribute) {
		Amendment amendment = new Amendment(member, amendmentTitle, "description", type, section, heading, title,
			content);

		amendment.setContribute(contribute);
		return amendment;
	}

	public static Contribute contribute() {
		return Contribute.createContribute();
	}
}
