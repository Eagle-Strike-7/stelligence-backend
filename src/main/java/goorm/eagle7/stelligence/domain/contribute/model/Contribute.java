package goorm.eagle7.stelligence.domain.contribute.model;

import static lombok.AccessLevel.*;

import java.util.ArrayList;
import java.util.List;

import goorm.eagle7.stelligence.common.entity.BaseTimeEntity;
import goorm.eagle7.stelligence.domain.amendment.model.Amendment;
import goorm.eagle7.stelligence.domain.document.content.model.Document;
import goorm.eagle7.stelligence.domain.member.model.Member;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Contribute
 * 투표를 받고 문서 수정의 단위가 되는 수정안의 집합입니다.
 * 사용자로부터 수정안의 조합을 받아 생성됩니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Contribute extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "contribute_id")
	private Long id;

	@Enumerated(EnumType.STRING)
	private ContributeStatus status;

	/**
	 * Contribute가 가지는 수정안들의 목록입니다.
	 */
	@OneToMany(mappedBy = "contribute", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Amendment> amendments = new ArrayList<>();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "document_id")
	private Document document;

	private String title;
	private String description;

	/**
	 * 변경될 제목입니다.
	 */
	private String newDocumentTitle;

	/**
	 * 변경될 부모 문서의 ID입니다.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "new_parent_document_id")
	private Document newParentDocument;

	private Contribute(ContributeStatus status, Member member, Document document,
		String title, String description, String newDocumentTitle, Document newParentDocument) {
		this.status = status;
		this.member = member;
		this.document = document;
		this.title = title;
		this.description = description;
		this.newDocumentTitle = newDocumentTitle;
		this.newParentDocument = newParentDocument;
	}

	//===생성===//
	public static Contribute createContribute(Member member, Document document,
		String title, String description, String newDocumentTitle, Document newParentDocument) {
		return new Contribute(
			ContributeStatus.VOTING,
			member,
			document,
			title,
			description,
			newDocumentTitle,
			newParentDocument
		);
	}

	//수정안 리스트에 추가
	public void addAmendment(Amendment amendment) {
		amendments.add(amendment);
		amendment.setContribute(this);
	}

	//삭제할 권한이 있는지 확인
	public boolean hasPermissionToDelete(Long memberId) {
		return this.getMember().getId().equals(memberId);
	}

	//투표 중인지 확인
	public boolean isVoting() {
		return this.getStatus() == ContributeStatus.VOTING;
	}

	public void setStatusDebating() {
		if (this.status != ContributeStatus.VOTING) {
			throw new IllegalStateException("투표 중인 Contribute만 상태를 변경할 수 있습니다.");
		}
		this.status = ContributeStatus.DEBATING;
	}

	public void setStatusRejected() {
		if (this.status != ContributeStatus.VOTING) {
			throw new IllegalStateException("투표 중인 Contribute만 상태를 변경할 수 있습니다.");
		}
		this.status = ContributeStatus.REJECTED;
	}

	public void setStatusMerged() {
		if (this.status != ContributeStatus.VOTING) {
			throw new IllegalStateException("투표 중인 Contribute만 상태를 변경할 수 있습니다.");
		}
		this.status = ContributeStatus.MERGED;
	}
}
