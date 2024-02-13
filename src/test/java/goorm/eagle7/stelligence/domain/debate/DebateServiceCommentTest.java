package goorm.eagle7.stelligence.domain.debate;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import goorm.eagle7.stelligence.api.exception.BaseException;
import goorm.eagle7.stelligence.config.mockdata.TestFixtureGenerator;
import goorm.eagle7.stelligence.domain.debate.dto.CommentRequest;
import goorm.eagle7.stelligence.domain.debate.dto.CommentResponse;
import goorm.eagle7.stelligence.domain.debate.event.NewCommentEvent;
import goorm.eagle7.stelligence.domain.debate.model.Comment;
import goorm.eagle7.stelligence.domain.debate.model.Debate;
import goorm.eagle7.stelligence.domain.debate.model.DebateStatus;
import goorm.eagle7.stelligence.domain.debate.repository.CommentRepository;
import goorm.eagle7.stelligence.domain.debate.repository.DebateRepository;
import goorm.eagle7.stelligence.domain.member.MemberRepository;
import goorm.eagle7.stelligence.domain.member.model.Member;

@ExtendWith(MockitoExtension.class)
class DebateServiceCommentTest {

	@Mock
	private DebateRepository debateRepository;
	@Mock
	private MemberRepository memberRepository;
	@Mock
	private CommentRepository commentRepository;
	@Mock
	private ApplicationEventPublisher applicationEventPublisher;

	@InjectMocks
	private DebateService debateService;

	@Test
	@DisplayName("열린 토론에 댓글 등록")
	void addComment() {
		// given
		String commentContent = "댓글 내용1";
		CommentRequest commentRequest = CommentRequest.of(commentContent);
		LocalDateTime createdAt = LocalDateTime.of(2024, 1, 14, 1, 0);
		LocalDateTime endAt = LocalDateTime.of(2024, 1, 14, 2, 0);
		LocalDateTime commentedAt = LocalDateTime.of(2024, 1, 14, 3, 0);

		Long debateId = 1L;
		Long memberId = 2L;
		Member commenter = TestFixtureGenerator.member(memberId, "commenter1");
		Debate debate = TestFixtureGenerator.debate(debateId, null, DebateStatus.OPEN, endAt, 1, createdAt);

		try (MockedStatic<LocalDateTime> mockedLocalDateTime = Mockito.mockStatic(LocalDateTime.class)) {
			mockedLocalDateTime.when(LocalDateTime::now).thenReturn(commentedAt);

			when(memberRepository.findById(memberId)).thenReturn(Optional.of(commenter));
			when(debateRepository.findDebateByIdForUpdate(debateId)).thenReturn(Optional.of(debate));

			// when
			debateService.addComment(commentRequest, debateId, memberId);

			// then
			// 댓글은 commentRepository의 save 메서드에 의해 저장된다.
			verify(commentRepository, times(1)).save(any(Comment.class));
			verify(debateRepository, times(1)).findDebateByIdForUpdate(debateId);
			verify(memberRepository, times(1)).findById(memberId);
			verify(applicationEventPublisher, times(1)).publishEvent(any(NewCommentEvent.class));

			assertThat(debate.getComments()).isNotEmpty();
			// 댓글을 작성하고 나면 debate의 종료 예상 시간이 댓글 작성 시점을 기준으로 토론 기간이 연장된다.
			assertThat(debate.getEndAt()).isEqualTo(commentedAt.plusMinutes(Debate.DEBATE_EXTENSION_DURATION_MINUTE));
			// 댓글을 작성하고 나면 debate의 commentSequence가 1 증가한다.
			assertThat(debate.getCommentSequence()).isEqualTo(2);
		}
	}

	@Test
	@DisplayName("토론에는 최대 지속 시간이 있다")
	void addCommentWithoutUpdatingEndAt() {
		// given
		String commentContent = "댓글 내용1";
		CommentRequest commentRequest = CommentRequest.of(commentContent);
		LocalDateTime createdAt = LocalDateTime.of(2024, 1, 14, 1, 0);
		LocalDateTime endAt = LocalDateTime.of(2024, 1, 14, 2, 0);
		LocalDateTime commentedAt = createdAt
			.plusMinutes(Debate.DEBATE_LIMIT_DURATION_MINUTE)
			.minusMinutes(Debate.DEBATE_EXTENSION_DURATION_MINUTE)
			.plusMinutes(1L);

		Long debateId = 1L;
		Long memberId = 2L;
		Member commenter = TestFixtureGenerator.member(memberId, "commenter1");
		Debate debate = TestFixtureGenerator.debate(debateId, null, DebateStatus.OPEN, endAt, 1, createdAt);

		try (MockedStatic<LocalDateTime> mockedLocalDateTime = Mockito.mockStatic(LocalDateTime.class)) {
			mockedLocalDateTime.when(LocalDateTime::now).thenReturn(commentedAt);

			when(memberRepository.findById(memberId)).thenReturn(Optional.of(commenter));
			when(debateRepository.findDebateByIdForUpdate(debateId)).thenReturn(Optional.of(debate));

			// when
			debateService.addComment(commentRequest, debateId, memberId);

			// then
			// 토론의 최대 지속시간은 7일이다.
			assertThat(debate.getEndAt()).isEqualTo(createdAt.plusMinutes(Debate.DEBATE_LIMIT_DURATION_MINUTE));
			assertThat(debate.getEndAt())
				.isNotEqualTo(commentedAt.plusMinutes(Debate.DEBATE_EXTENSION_DURATION_MINUTE));
		}
	}

	@Test
	@DisplayName("닫힌 토론에 댓글")
	void addCommentInClosedDebate() {
		// given
		String commentContent = "댓글 내용1";
		CommentRequest commentRequest = CommentRequest.of(commentContent);
		LocalDateTime endAt = LocalDateTime.of(2024, 1, 14, 2, 0);

		Long debateId = 1L;
		Long memberId = 2L;
		Debate debate = TestFixtureGenerator.debate(debateId, null, DebateStatus.CLOSED, endAt, 1);

		// when
		when(debateRepository.findDebateByIdForUpdate(debateId)).thenReturn(Optional.of(debate));

		// then
		// 이미 닫힌 토론에는 댓글을 작성할 수 없다.
		assertThatThrownBy(() -> debateService.addComment(commentRequest, debateId, memberId))
			.isInstanceOf(BaseException.class)
			.hasMessage("이미 닫힌 토론에 대한 댓글 작성요청입니다. Debate ID: " + debateId);
	}

	@Test
	@DisplayName("토론 댓글 삭제")
	void deleteComment() {
		// given
		String commentContent = "댓글 내용1";
		Long commentId = 1L;
		Long debateId = 2L;
		Long commenterId = 3L;
		Member commenter = TestFixtureGenerator.member(commenterId, "commenter1");
		Debate debate = TestFixtureGenerator.debate(debateId, null, DebateStatus.OPEN, null, 1, null);
		Comment comment = TestFixtureGenerator.comment(commentId, debate, commenter, commentContent, 1);

		when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

		// when
		debateService.deleteComment(commentId, commenterId);

		// then
		// 댓글의 작성자는 댓글을 삭제할 수 있다.
		verify(commentRepository, times(1)).findById(commentId);
		verify(commentRepository, times(1)).delete(comment);
	}

	@Test
	@DisplayName("토론 댓글 삭제 권한 없음")
	void deleteOtherMemberComment() {
		// given
		String commentContent = "댓글 내용1";
		Long commentId = 1L;
		Long debateId = 2L;
		Long commenterId = 3L;
		Long attackerId = 4L;
		Member commenter = TestFixtureGenerator.member(commenterId, "commenter1");
		Debate debate = TestFixtureGenerator.debate(debateId, null, DebateStatus.OPEN, null, 1, null);
		Comment comment = TestFixtureGenerator.comment(commentId, debate, commenter, commentContent, 1);

		when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

		// when

		// then
		// 권한이 없는 댓글은 삭제할 수 없다.
		assertThatThrownBy(() -> debateService.deleteComment(commentId, attackerId))
			.isInstanceOf(BaseException.class)
			.hasMessage("댓글에 대한 삭제 권한이 없습니다. Member ID: " + attackerId);
	}

	@Test
	@DisplayName("댓글 수정 테스트")
	void updateComment() {
		// given
		String originalCommentContent = "댓글 내용1";
		String updatedCommentContent = "수정된 댓글 내용1";
		CommentRequest commentRequest = CommentRequest.of(updatedCommentContent);
		Long commentId = 1L;
		Long debateId = 2L;
		Long commenterId = 3L;
		Member commenter = TestFixtureGenerator.member(commenterId, "commenter1");
		Debate debate = TestFixtureGenerator.debate(debateId, null, DebateStatus.OPEN, null, 1, null);
		Comment comment = TestFixtureGenerator.comment(commentId, debate, commenter, originalCommentContent, 1);

		when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

		// when
		CommentResponse commentResponse = debateService.updateComment(commentId, commentRequest, commenterId);

		// then
		// 댓글이 수정되면 댓글의 내용이 변경된다.
		verify(commentRepository, times(1)).findById(commentId);
		assertThat(comment.getContent()).isEqualTo(updatedCommentContent);
		assertThat(commentResponse.getContent()).isEqualTo(updatedCommentContent);
	}

	@Test
	@DisplayName("토론 댓글 수정 권한 없음")
	void updateOtherMemberComment() {
		// given
		String originalCommentContent = "댓글 내용1";
		String updatedCommentContent = "수정된 댓글 내용1";
		CommentRequest commentRequest = CommentRequest.of(updatedCommentContent);
		Long commentId = 1L;
		Long debateId = 2L;
		Long commenterId = 3L;
		Long attackerId = 4L;
		Member commenter = TestFixtureGenerator.member(commenterId, "commenter1");
		Debate debate = TestFixtureGenerator.debate(debateId, null, DebateStatus.OPEN, null, 1, null);
		Comment comment = TestFixtureGenerator.comment(commentId, debate, commenter, originalCommentContent, 1);

		when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

		// when

		// then
		// 권한이 없는 댓글은 수정할 수 없고, 내용이 변경되지 않는다.
		assertThatThrownBy(() -> debateService.updateComment(commentId, commentRequest, attackerId))
			.isInstanceOf(BaseException.class)
			.hasMessage("댓글에 대한 수정 권한이 없습니다. Member ID: " + attackerId);
		assertThat(comment.getContent()).isEqualTo(originalCommentContent);
	}

	@Test
	@DisplayName("토론 댓글 리스트 조회")
	void getComments() {
		// given
		Long debateId = 1L;
		when(commentRepository.findAllByDebateId(1L)).thenReturn(List.of());

		// when
		List<CommentResponse> comments = debateService.getComments(debateId);

		// then
		verify(commentRepository, times(1)).findAllByDebateId(debateId);
	}

	@Test
	@DisplayName("댓글 최대 길이 초과 테스트")
	void commentMaxLengthExceed() {
		//given
		final int length = Comment.MAX_COMMENT_LENGTH + 1;
		final String content = getKorStringWithLength(length);
		assertThat(content.length()).isGreaterThan(Comment.MAX_COMMENT_LENGTH);

		CommentRequest commentRequest = CommentRequest.of(content);

		Long debateId = 1L;
		Long commenterId = 1L;
		Debate debate = TestFixtureGenerator.debate(debateId, null, DebateStatus.OPEN,
			LocalDateTime.now(), 1, LocalDateTime.now());
		Member commenter = TestFixtureGenerator.member(commenterId, "commenter");

		//when

		//then
		assertThatThrownBy(() -> debateService.addComment(commentRequest, debateId, commenterId))
			.isInstanceOf(BaseException.class)
			.hasMessage("토론 댓글의 최대 길이는 " + Comment.MAX_COMMENT_LENGTH + " 자 입니다.");

	}

	@Test
	@DisplayName("댓글 최대 길이 성공 테스트")
	void commentMaxLengthPass() {
		//given
		final int length = Comment.MAX_COMMENT_LENGTH;
		final String content = getKorStringWithLength(length);
		assertThat(content.length()).isLessThanOrEqualTo(Comment.MAX_COMMENT_LENGTH);

		CommentRequest commentRequest = CommentRequest.of(content);
		Debate debate = TestFixtureGenerator.debate(1L, null, DebateStatus.OPEN, LocalDateTime.now(),
			1, LocalDateTime.now());
		Member commenter = TestFixtureGenerator.member(2L, "commenter");

		when(memberRepository.findById(commenter.getId())).thenReturn(Optional.of(commenter));
		when(debateRepository.findDebateByIdForUpdate(debate.getId())).thenReturn(Optional.of(debate));

		//when
		debateService.addComment(commentRequest, debate.getId(), commenter.getId());

		//then
		verify(commentRepository, times(1)).save(any(Comment.class));

	}

	@Test
	@DisplayName("빈 댓글 테스트")
	void emptyComment() {
		//given
		final String content = " ";

		CommentRequest commentRequest = CommentRequest.of(content);

		Long debateId = 1L;
		Long commenterId = 1L;
		Debate debate = TestFixtureGenerator.debate(debateId, null, DebateStatus.OPEN,
			LocalDateTime.now(), 1, LocalDateTime.now());
		Member commenter = TestFixtureGenerator.member(commenterId, "commenter");

		//when

		//then
		assertThatThrownBy(() -> debateService.addComment(commentRequest, debateId, commenterId))
			.isInstanceOf(BaseException.class)
			.hasMessage("댓글에 내용이 존재하지 않습니다.");

	}

	@Test
	@DisplayName("null 댓글 테스트")
	void nullComment() {
		//given
		final String content = null;

		CommentRequest commentRequest = CommentRequest.of(content);

		Long debateId = 1L;
		Long commenterId = 1L;
		Debate debate = TestFixtureGenerator.debate(debateId, null, DebateStatus.OPEN,
			LocalDateTime.now(), 1, LocalDateTime.now());
		Member commenter = TestFixtureGenerator.member(commenterId, "commenter");

		//when

		//then
		assertThatThrownBy(() -> debateService.addComment(commentRequest, debateId, commenterId))
			.isInstanceOf(BaseException.class)
			.hasMessage("댓글에 내용이 존재하지 않습니다.");

	}

	private static String getKorStringWithLength(int length) {
		String content = "안녕하세요 이영민입니다 반갑습니다~\n";
		assertThat(content).hasSize(20);

		StringBuilder stringBuilder = new StringBuilder();
		while (stringBuilder.length() < length) {
			stringBuilder.append(content);
		}
		return stringBuilder.substring(0, length);
	}
}
