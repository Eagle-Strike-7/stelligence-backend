package goorm.eagle7.stelligence.domain.commit.model;

import goorm.eagle7.stelligence.common.entity.BaseTimeEntity;
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
public class Commit extends BaseTimeEntity {

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

	private String commitTitle;
	private String commitDescription;

	@Enumerated(EnumType.STRING)
	private CommitType type;

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
	private CommitStatus status;

	//수정안 생성(update, create)
	public Commit(Member member, String commitTitle, String commitDescription, CommitType type, Section targetSection,
		Heading newSectionHeading, String newSectionTitle, String newSectionContent) {
		this.member = member;
		this.commitTitle = commitTitle;
		this.commitDescription = commitDescription;
		this.type = type;
		this.targetSection = targetSection;
		this.newSectionHeading = newSectionHeading;
		this.newSectionTitle = newSectionTitle;
		this.newSectionContent = newSectionContent;
		//기본값 pending
		this.status = CommitStatus.PENDING;
	}

	//수정안 생성(delete)
	public Commit(Member member, String commitTitle, String commitDescription, CommitType type, Section targetSection) {
		this.member = member;
		this.commitTitle = commitTitle;
		this.commitDescription = commitDescription;
		this.type = type;
		this.targetSection = targetSection;
		this.status = CommitStatus.PENDING;
	}
}
