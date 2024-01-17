package goorm.eagle7.stelligence.domain.contribute.model;

import static lombok.AccessLevel.*;

import java.util.ArrayList;
import java.util.List;

import goorm.eagle7.stelligence.common.entity.BaseTimeEntity;
import goorm.eagle7.stelligence.domain.amendment.model.Amendment;
import goorm.eagle7.stelligence.domain.vote.model.Vote;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
	private Long id;

	@Enumerated(EnumType.STRING)
	private ContributeStatus status;

	/**
	 * Contribute가 가지는 수정안들의 목록입니다.
	 */
	@OneToMany(mappedBy = "contribute")
	private List<Amendment> amendments = new ArrayList<>();

	/**
	 * 해당 수정 요청에 대한 투표 목록을 가지고 있습니다.
	 * 효율성을 위해 Vote -> Contribute 일방향 연관관계만 유지할 수도 있습니다.
	 */
	@OneToMany(mappedBy = "contribute")
	private List<Vote> votes = new ArrayList<>();

	//===생성===//
	public static Contribute createContribute() {
		Contribute contribute = new Contribute();
		contribute.status = ContributeStatus.VOTING;
		return contribute;
	}

}
