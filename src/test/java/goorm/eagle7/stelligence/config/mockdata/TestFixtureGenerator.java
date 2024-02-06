package goorm.eagle7.stelligence.config.mockdata;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Set;

import goorm.eagle7.stelligence.domain.amendment.model.Amendment;
import goorm.eagle7.stelligence.domain.amendment.model.AmendmentType;
import goorm.eagle7.stelligence.domain.bookmark.model.Bookmark;
import goorm.eagle7.stelligence.domain.contribute.model.Contribute;
import goorm.eagle7.stelligence.domain.contribute.model.ContributeStatus;
import goorm.eagle7.stelligence.domain.debate.model.Comment;
import goorm.eagle7.stelligence.domain.debate.model.Debate;
import goorm.eagle7.stelligence.domain.debate.model.DebateStatus;
import goorm.eagle7.stelligence.domain.document.content.model.Document;
import goorm.eagle7.stelligence.domain.member.model.Badge;
import goorm.eagle7.stelligence.domain.member.model.Member;
import goorm.eagle7.stelligence.domain.member.model.Role;
import goorm.eagle7.stelligence.domain.member.model.SocialType;
import goorm.eagle7.stelligence.domain.section.model.Heading;
import goorm.eagle7.stelligence.domain.section.model.Section;

/**
 * 테스트를 위한 객체 생성기입니다.
 * Reflection을 사용하여 객체를 생성합니다.
 */
public class TestFixtureGenerator {

	private TestFixtureGenerator() {
	}

	public static Document document(Long id, Member author, String title, Long currentRevision) {
		try {
			Class<?> documentClass = Class.forName("goorm.eagle7.stelligence.domain.document.content.model.Document");

			Constructor<?> constructor = documentClass.getDeclaredConstructor();
			constructor.setAccessible(true);

			// 객체 생성
			Object document = constructor.newInstance();

			// 필드 조작
			Field idField = documentClass.getDeclaredField("id");
			Field titleField = documentClass.getDeclaredField("title");
			Field authorField = documentClass.getDeclaredField("author");
			Field currentRevisionField = documentClass.getDeclaredField("currentRevision");
			Field sectionsField = documentClass.getDeclaredField("sections");

			idField.setAccessible(true);
			titleField.setAccessible(true);
			authorField.setAccessible(true);
			currentRevisionField.setAccessible(true);
			sectionsField.setAccessible(true);

			idField.set(document, id);
			titleField.set(document, title);
			authorField.set(document, author); // Member 객체 필요
			currentRevisionField.set(document, currentRevision);

			return (Document)document;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Member member(Long id, Role role, long contributes, String name, String nickname, String email,
		String imageUrl, String socialId, SocialType socialType, String refreshToken, Set<Badge> badges) {
		try {
			Class<?> memberClass = Class.forName("goorm.eagle7.stelligence.domain.member.model.Member");

			Constructor<?> constructor = memberClass.getDeclaredConstructor();
			constructor.setAccessible(true);

			// 객체 생성
			Object member = constructor.newInstance();

			// 필드 조작
			Field idField = memberClass.getDeclaredField("id");
			Field roleField = memberClass.getDeclaredField("role");
			Field contributesField = memberClass.getDeclaredField("contributes");
			Field nameField = memberClass.getDeclaredField("name");
			Field nicknameField = memberClass.getDeclaredField("nickname");
			Field emailField = memberClass.getDeclaredField("email");
			Field imageUrlField = memberClass.getDeclaredField("imageUrl");
			Field socialIdField = memberClass.getDeclaredField("socialId");
			Field socialTypeField = memberClass.getDeclaredField("socialType");
			Field refreshTokenField = memberClass.getDeclaredField("refreshToken");
			Field badgesField = memberClass.getDeclaredField("badges");

			idField.setAccessible(true);
			roleField.setAccessible(true);
			contributesField.setAccessible(true);
			nameField.setAccessible(true);
			nicknameField.setAccessible(true);
			emailField.setAccessible(true);
			imageUrlField.setAccessible(true);
			socialIdField.setAccessible(true);
			socialTypeField.setAccessible(true);
			refreshTokenField.setAccessible(true);
			badgesField.setAccessible(true);

			idField.set(member, id);
			roleField.set(member, role);
			contributesField.set(member, contributes);
			nameField.set(member, name);
			nicknameField.set(member, nickname);
			emailField.set(member, email);
			imageUrlField.set(member, imageUrl);
			socialIdField.set(member, socialId);
			socialTypeField.set(member, socialType);
			refreshTokenField.set(member, refreshToken);
			badgesField.set(member, badges);

			return (Member)member;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Member member(Long id, String nickname) {
		return TestFixtureGenerator.member(id, Role.USER, 0L, "name", nickname, "email", "imageUrl", "socialId",
			SocialType.KAKAO, "refreshToken", Set.of());
	}

	public static Section section(Long id, Long revision, Document document, Heading heading, String title,
		String content, int order) {

		try {
			Class<?> sectionClazz = Class.forName("goorm.eagle7.stelligence.domain.section.model.Section");

			Constructor<?> constructor = sectionClazz.getDeclaredConstructor();
			constructor.setAccessible(true);

			Object section = constructor.newInstance();

			Field idField = sectionClazz.getDeclaredField("id");
			Field revisionField = sectionClazz.getDeclaredField("revision");
			Field documentField = sectionClazz.getDeclaredField("document");
			Field headingField = sectionClazz.getDeclaredField("heading");
			Field titleField = sectionClazz.getDeclaredField("title");
			Field contentField = sectionClazz.getDeclaredField("content");
			Field orderField = sectionClazz.getDeclaredField("order");

			idField.setAccessible(true);
			documentField.setAccessible(true);
			revisionField.setAccessible(true);
			headingField.setAccessible(true);
			titleField.setAccessible(true);
			contentField.setAccessible(true);
			orderField.setAccessible(true);

			idField.set(section, id);
			documentField.set(section, document);
			revisionField.set(section, revision);
			headingField.set(section, heading);
			titleField.set(section, title);
			contentField.set(section, content);
			orderField.set(section, order);

			return (Section)section;

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Amendment amendment(Long id, Contribute contribute, AmendmentType type, Section section,
		Heading heading, String title, String content, Integer creatingOrder) {

		try {
			Class<?> amendmentClazz = Class.forName("goorm.eagle7.stelligence.domain.amendment.model.Amendment");

			Constructor<?> constructor = amendmentClazz.getDeclaredConstructor();
			constructor.setAccessible(true);

			Object amendment = constructor.newInstance();

			Field idField = amendmentClazz.getDeclaredField("id");
			Field contributeField = amendmentClazz.getDeclaredField("contribute");
			Field typeField = amendmentClazz.getDeclaredField("type");
			Field sectionField = amendmentClazz.getDeclaredField("targetSection");
			Field headingField = amendmentClazz.getDeclaredField("newSectionHeading");
			Field titleField = amendmentClazz.getDeclaredField("newSectionTitle");
			Field contentField = amendmentClazz.getDeclaredField("newSectionContent");
			Field creatingOrderField = amendmentClazz.getDeclaredField("creatingOrder");

			idField.setAccessible(true);
			contributeField.setAccessible(true);
			typeField.setAccessible(true);
			sectionField.setAccessible(true);
			headingField.setAccessible(true);
			titleField.setAccessible(true);
			contentField.setAccessible(true);
			creatingOrderField.setAccessible(true);

			idField.set(amendment, id);
			contributeField.set(amendment, contribute);
			typeField.set(amendment, type);
			sectionField.set(amendment, section);
			headingField.set(amendment, heading);
			titleField.set(amendment, title);
			contentField.set(amendment, content);
			creatingOrderField.set(amendment, creatingOrder);

			if (contribute != null) {
				contribute.getAmendments().add((Amendment)amendment);
			}

			return (Amendment)amendment;

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Contribute contribute(Long id, Member contributor, ContributeStatus status, Document document) {
		return contribute(id, contributor, status, document, "newTitle", document);
	}

	public static Contribute contribute(Long id, Member contributor, ContributeStatus status, Document document,
		String newDocumentTitle, Document newParentDocument) {
		try {
			Class<?> contributeClazz = Class.forName("goorm.eagle7.stelligence.domain.contribute.model.Contribute");

			Constructor<?> constructor = contributeClazz.getDeclaredConstructor();
			constructor.setAccessible(true);

			Object contribute = constructor.newInstance();

			Field idField = contributeClazz.getDeclaredField("id");
			Field contributorField = contributeClazz.getDeclaredField("member");
			Field statusField = contributeClazz.getDeclaredField("status");
			Field documentField = contributeClazz.getDeclaredField("document");
			Field amendmentsField = contributeClazz.getDeclaredField("amendments");
			Field newDocumentTitleField = contributeClazz.getDeclaredField("newDocumentTitle");
			Field newParentDocumentField = contributeClazz.getDeclaredField("newParentDocument");

			idField.setAccessible(true);
			contributorField.setAccessible(true);
			statusField.setAccessible(true);
			documentField.setAccessible(true);
			amendmentsField.setAccessible(true);
			newDocumentTitleField.setAccessible(true);
			newParentDocumentField.setAccessible(true);

			idField.set(contribute, id);
			contributorField.set(contribute, contributor);
			statusField.set(contribute, status);
			documentField.set(contribute, document);
			amendmentsField.set(contribute, new ArrayList<>());
			newDocumentTitleField.set(contribute, newDocumentTitle);
			newParentDocumentField.set(contribute, newParentDocument);

			return (Contribute)contribute;

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Debate debate(Long id, Contribute contribute, DebateStatus status, LocalDateTime endAt,
		int commentSequence) {

		try {
			Class<?> debateClazz = Class.forName("goorm.eagle7.stelligence.domain.debate.model.Debate");

			Constructor<?> constructor = debateClazz.getDeclaredConstructor();
			constructor.setAccessible(true);

			Object debate = constructor.newInstance();

			Field idField = debateClazz.getDeclaredField("id");
			Field contributeField = debateClazz.getDeclaredField("contribute");
			Field statusField = debateClazz.getDeclaredField("status");
			Field endAtField = debateClazz.getDeclaredField("endAt");
			Field commentSequenceField = debateClazz.getDeclaredField("commentSequence");
			Field commentsField = debateClazz.getDeclaredField("comments");

			idField.setAccessible(true);
			contributeField.setAccessible(true);
			statusField.setAccessible(true);
			endAtField.setAccessible(true);
			commentSequenceField.setAccessible(true);
			commentsField.setAccessible(true);

			idField.set(debate, id);
			contributeField.set(debate, contribute);
			statusField.set(debate, status);
			endAtField.set(debate, endAt);
			commentSequenceField.set(debate, commentSequence);
			commentsField.set(debate, new ArrayList<>());

			return (Debate)debate;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public static Debate debate(Long id, Contribute contribute, DebateStatus status, LocalDateTime endAt,
		int commentSequence, LocalDateTime createdAt) {

		try {
			Debate debate = TestFixtureGenerator.debate(id, contribute, status, endAt, commentSequence);

			Class<?> debateClazz = Class.forName("goorm.eagle7.stelligence.domain.debate.model.Debate");

			Class<?> baseTimeEntityClazz = debateClazz.getSuperclass();
			Field createdAtField = baseTimeEntityClazz.getDeclaredField("createdAt");
			createdAtField.setAccessible(true);
			createdAtField.set(debate, createdAt);

			return (Debate)debate;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public static Comment comment(Long id, Debate debate, Member member, String content, int sequence) {
		try {
			Class<?> commentClazz = Class.forName("goorm.eagle7.stelligence.domain.debate.model.Comment");

			Constructor<?> constructor = commentClazz.getDeclaredConstructor();
			constructor.setAccessible(true);

			Object comment = constructor.newInstance();

			Field idField = commentClazz.getDeclaredField("id");
			Field debateField = commentClazz.getDeclaredField("debate");
			Field commenterField = commentClazz.getDeclaredField("commenter");
			Field contentField = commentClazz.getDeclaredField("content");
			Field sequenceField = commentClazz.getDeclaredField("sequence");

			idField.setAccessible(true);
			debateField.setAccessible(true);
			commenterField.setAccessible(true);
			contentField.setAccessible(true);
			sequenceField.setAccessible(true);

			idField.set(comment, id);
			debateField.set(comment, debate);
			commenterField.set(comment, member);
			contentField.set(comment, content);
			sequenceField.set(comment, sequence);

			return (Comment)comment;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	// Bookmark 생성
	public static Bookmark bookmark(Long id, Member member, Document document) {
		try {
			Class<?> bookmarkClazz = Class.forName("goorm.eagle7.stelligence.domain.bookmark.model.Bookmark");

			Constructor<?> constructor = bookmarkClazz.getDeclaredConstructor();
			constructor.setAccessible(true);

			Object bookmark = constructor.newInstance();

			Field idField = bookmarkClazz.getDeclaredField("id");
			Field memberField = bookmarkClazz.getDeclaredField("member");
			Field documentField = bookmarkClazz.getDeclaredField("document");

			idField.setAccessible(true);
			memberField.setAccessible(true);
			documentField.setAccessible(true);

			idField.set(bookmark, id);
			memberField.set(bookmark, member);
			documentField.set(bookmark, document);

			return (Bookmark)bookmark;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
