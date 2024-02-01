package goorm.eagle7.stelligence.domain.debate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.api.exception.BaseException;
import goorm.eagle7.stelligence.domain.contribute.model.Contribute;
import goorm.eagle7.stelligence.domain.debate.dto.CommentCreateRequest;
import goorm.eagle7.stelligence.domain.debate.dto.DebatePageResponse;
import goorm.eagle7.stelligence.domain.debate.dto.DebateResponse;
import goorm.eagle7.stelligence.domain.debate.model.Comment;
import goorm.eagle7.stelligence.domain.debate.model.Debate;
import goorm.eagle7.stelligence.domain.debate.model.DebateStatus;
import goorm.eagle7.stelligence.domain.member.MemberRepository;
import goorm.eagle7.stelligence.domain.member.model.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DebateService {

	private final DebateRepository debateRepository;
	private final CommentRepository commentRepository;
	private final MemberRepository memberRepository;

	/**
	 * 수정요청을 토론으로 전환합니다.
	 * 이때의 수정요청은 투표중인 상태여야합니다.
	 * 수정요청의 스케쥴러에 의해서만 호출되어야하는 메서드입니다.
	 * @param contribute: 토론으로 전환할 수정 요청
	 */
	public void convertToDebate(Contribute contribute) {
		Debate debate = Debate.openFrom(contribute);
		debateRepository.save(debate);
	}

	/**
	 * 특정 토론을 ID로 찾아서 종료합니다.
	 * 토론의 스케쥴러에 의해서만 호출되어야하는 메서드입니다.
	 * @param debateId: 종료할 토론의 ID
	 */
	public void closeDebateById(Long debateId) {
		Debate targetDebate = debateRepository.findById(debateId)
			.orElseThrow(() -> new IllegalArgumentException("삭제하려는 토론이 존재하지 않습니다. Debate ID: " + debateId));

		targetDebate.close();
	}

	/**
	 * 특정 토론을 ID로 찾아서 조회합니다.
	 * @param debateId: 조회할 토론의 ID
	 * @return DebateResponse: 조회된 토론 응답 DTO
	 */
	@Transactional(readOnly = true)
	public DebateResponse getDebateDetailById(Long debateId) {
		Debate findDebate = debateRepository.findByIdWithContribute(debateId)
			.orElseThrow(() -> new BaseException("존재하지 않는 토론에 대한 조회 요청입니다. Debate ID: " + debateId));
		return DebateResponse.of(findDebate);
	}

	/**
	 * 토론의 상태(OPEN / CLOSED)에 따라 토론 리스트를 페이징을 적용하여 조회합니다.
	 * @param status: 조회하려는 토론의 상태(OPEN / CLOSED)
	 * @param pageable: 조회하려는 토론의 페이지 정보
	 * @return DebatePageResponse: 조회된 토론 페이지 응답 DTO
	 */
	@Transactional(readOnly = true)
	public DebatePageResponse getDebatePage(DebateStatus status, Pageable pageable) {

		Page<Debate> debatePage = debateRepository.findPageByStatus(status, pageable);
		return DebatePageResponse.from(debatePage);
	}

	/**
	 * 특정 열린 토론에 댓글을 작성합니다.
	 * @param commentCreateRequest: 댓글 작성에 필요한 정보를 담은 요청 DTO
	 * @param debateId: 댓글을 달 토론의 ID
	 * @param loginMemberId: 현재 로그인한 회원의 ID
	 */
	public void addComment(CommentCreateRequest commentCreateRequest, Long debateId, Long loginMemberId) {

		Debate findDebate = debateRepository.findById(debateId)
			.orElseThrow(() -> new BaseException("존재하지 않는 토론에 대한 댓글 작성요청입니다. Debate ID: " + debateId));

		if (DebateStatus.CLOSED.equals(findDebate.getStatus())) {
			throw new BaseException("이미 닫힌 토론에 대한 댓글 작성요청입니다. Debate ID: " + debateId);
		}

		Member loginMember = memberRepository.findById(loginMemberId)
			.orElseThrow(() -> new BaseException("존재하지 않는 회원에 대한 댓글 작성요청입니다. Member ID: " + loginMemberId));

		Comment comment = Comment.createComment(commentCreateRequest.getContent(), findDebate, loginMember);
		commentRepository.save(comment);
	}

	/**
	 * 특정 토론 댓글을 삭제합니다. 댓글을 작성했던 회원만이 삭제할 수 있습니다.
	 * @param commentId: 삭제할 댓글의 ID
	 * @param loginMemberId: 로그인한 회원의 ID
	 */
	public void deleteComment(Long commentId, Long loginMemberId) {

		Comment targetComment = commentRepository.findById(commentId)
			.orElseThrow(() -> new BaseException("존재하지 않는 댓글에 대한 삭제 요청입니다. Comment ID: " + commentId));

		if (!targetComment.hasPermissionToDelete(loginMemberId)) {
			throw new BaseException("댓글에 대한 삭제 권한이 없습니다. Member ID: " + loginMemberId);
		}

		commentRepository.delete(targetComment);
	}
}
