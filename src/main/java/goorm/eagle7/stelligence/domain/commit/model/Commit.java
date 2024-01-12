package goorm.eagle7.stelligence.domain.commit.model;

import goorm.eagle7.stelligence.common.entity.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
	private Long commitId;

	@ManyToOne
	@JoinColumn(name = "member_id")
	private Member member;

	@ManyToOne
	@JoinColumn(name = "contribute_id")
	private Contributes contributes;

	private String commitTitle;
	private String description;

	@Enumerated(EnumType.STRING)
	private CommitType type;

	@ManyToOne
	@JoinColumns({
		@JoinColumn(name = "target_section_id", referencedColumnName = "section_id"),
		@JoinColumn(name = "target_revision", referencedColumnName = "revision")
	})
	private Section targetSection;

	@Enumerated(EnumType.STRING)
	private NewSectionHeading newSectionHeading;

	private String newSectionTitle;
	private String newSectionContent;

	@Enumerated(EnumType.STRING)
	private CommitStatus status;

	public Commit(Member member, String commitTitle, String description, NewSectionHeading newSectionHeading,
		String newSectionTitle, String newSectionContent) {
		this.member = member;
		this.commitTitle = commitTitle;
		this.description = description;
		this.newSectionHeading = newSectionHeading;
		this.newSectionTitle = newSectionTitle;
		this.newSectionContent = newSectionContent;
	}
}
