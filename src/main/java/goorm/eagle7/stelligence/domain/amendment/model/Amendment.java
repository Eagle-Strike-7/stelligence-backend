package goorm.eagle7.stelligence.domain.amendment.model;

import goorm.eagle7.stelligence.common.entity.BaseTimeEntity;
import goorm.eagle7.stelligence.domain.contribute.model.Contribute;
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
	@Column(name = "amendment_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "contribute_id")
	private Contribute contribute;

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

	//create type으로 생성할 때만 발생
	private Integer creatingOrder;

	//수정안 생성(create)
	private Amendment(AmendmentType type, Section targetSection, Heading newSectionHeading,
		String newSectionTitle, String newSectionContent, Integer creatingOrder) {
		this.type = type;
		this.targetSection = targetSection;
		this.newSectionHeading = newSectionHeading;
		this.newSectionTitle = newSectionTitle;
		this.newSectionContent = newSectionContent;
		this.creatingOrder = creatingOrder;
	}

	//수정안 생성(update)
	private Amendment(AmendmentType type, Section targetSection, Heading newSectionHeading,
		String newSectionTitle, String newSectionContent) {
		this.type = type;
		this.targetSection = targetSection;
		this.newSectionHeading = newSectionHeading;
		this.newSectionTitle = newSectionTitle;
		this.newSectionContent = newSectionContent;
	}

	//수정안 생성(delete)
	private Amendment(AmendmentType type, Section targetSection) {
		this.type = type;
		this.targetSection = targetSection;
	}

	public static Amendment forCreate(Section targetSection,
		Heading newSectionHeading, String newSectionTitle, String newSectionContent, Integer creatingOrder) {
		return new Amendment(
			AmendmentType.CREATE,
			targetSection,
			newSectionHeading,
			newSectionTitle,
			newSectionContent,
			creatingOrder
		);
	}

	public static Amendment forUpdate(Section targetSection,
		Heading newSectionHeading, String newSectionTitle, String newSectionContent) {
		return new Amendment(
			AmendmentType.UPDATE,
			targetSection,
			newSectionHeading,
			newSectionTitle,
			newSectionContent
		);
	}

	public static Amendment forDelete(Section targetSection) {
		return new Amendment(
			AmendmentType.DELETE,
			targetSection
		);
	}

	public void setContribute(Contribute contribute) {
		this.contribute = contribute;
	}
}
