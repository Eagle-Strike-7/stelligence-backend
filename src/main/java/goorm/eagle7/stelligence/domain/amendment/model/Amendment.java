package goorm.eagle7.stelligence.domain.amendment.model;

import goorm.eagle7.stelligence.common.entity.BaseTimeEntity;
import goorm.eagle7.stelligence.domain.contribute.model.Contribute;
import goorm.eagle7.stelligence.domain.member.model.Member;
import goorm.eagle7.stelligence.domain.section.model.Heading;
import goorm.eagle7.stelligence.domain.section.model.Section;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Amendment extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "commit_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "contribute_id")
	private Contribute contribute;

	private String amendmentTitle;
	private String amendmentDescription;

	@Enumerated(EnumType.STRING)
	private AmendmentType type;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name = "target_section_id", referencedColumnName = "section_id"),
		@JoinColumn(name = "target_revision", referencedColumnName = "revision")
	})
	private Section targetSection;

	@Enumerated(EnumType.STRING)
	private Heading newSectionHeading;

	private String newSectionTitle;
	private String newSectionContent;

	@Enumerated(EnumType.STRING)
	private AmendmentStatus status;

	//수정안 생성(update, create)
	public Amendment(Member member, String amendmentTitle, String amendmentDescription, AmendmentType type,
		Section targetSection,
		Heading newSectionHeading, String newSectionTitle, String newSectionContent) {
		this.member = member;
		this.amendmentTitle = amendmentTitle;
		this.amendmentDescription = amendmentDescription;
		this.type = type;
		this.targetSection = targetSection;
		this.newSectionHeading = newSectionHeading;
		this.newSectionTitle = newSectionTitle;
		this.newSectionContent = newSectionContent;
		//기본값 pending
		this.status = AmendmentStatus.PENDING;
	}

	//수정안 생성(delete)
	public Amendment(Member member, String amendmentTitle, String amendmentDescription, AmendmentType type,
		Section targetSection) {
		this.member = member;
		this.amendmentTitle = amendmentTitle;
		this.amendmentDescription = amendmentDescription;
		this.type = type;
		this.targetSection = targetSection;
		this.status = AmendmentStatus.PENDING;
	}

	//수정안 수정
	public Amendment updateContent(String amendmentTitle, String amendmentDescription,
		Heading newSectionHeading, String newSectionTitle, String newSectionContent) {

		//PENDING 상태가 아니면(REQUESTED) 수정할 수 없음
		if (this.status != AmendmentStatus.PENDING) {
			throw new IllegalStateException();
		}

		this.amendmentTitle = amendmentTitle;
		this.amendmentDescription = amendmentDescription;
		this.newSectionHeading = newSectionHeading;
		this.newSectionTitle = newSectionTitle;
		this.newSectionContent = newSectionContent;

		return this;
	}

	public void setContribute(Contribute contribute) {
		this.contribute = contribute;
	}
}
