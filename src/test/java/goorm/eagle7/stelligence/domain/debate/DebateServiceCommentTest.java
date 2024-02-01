package goorm.eagle7.stelligence.domain.debate;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import goorm.eagle7.stelligence.api.exception.BaseException;
import goorm.eagle7.stelligence.config.mockdata.TestFixtureGenerator;
import goorm.eagle7.stelligence.domain.debate.dto.CommentRequest;
import goorm.eagle7.stelligence.domain.debate.model.Comment;
import goorm.eagle7.stelligence.domain.debate.model.Debate;
import goorm.eagle7.stelligence.domain.debate.model.DebateStatus;
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
		Long memberId = 1L;
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

			assertThat(debate.getComments()).isNotEmpty();
			// 댓글을 작성하고 나면 debate의 종료 예상 시간이 댓글 작성 시점의 하루 뒤로 반영되어야한다.
			assertThat(debate.getEndAt()).isEqualTo(commentedAt.plusDays(1L));
			// 댓글을 작성하고 나면 debate의 commentSequence가 1 증가한다.
			assertThat(debate.getCommentSequence()).isEqualTo(2);
		}
	}

	@Test
	@DisplayName("토론의 최대 지속시간은 7일")
	void addCommentWithoutUpdatingEndAt() {
		// given
		String commentContent = "댓글 내용1";
		CommentRequest commentRequest = CommentRequest.of(commentContent);
		LocalDateTime createdAt = LocalDateTime.of(2024, 1, 14, 1, 0);
		LocalDateTime endAt = LocalDateTime.of(2024, 1, 14, 2, 0);
		LocalDateTime commentedAt = createdAt.plusDays(6L).plusHours(23L);

		Long debateId = 1L;
		Long memberId = 1L;
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
			assertThat(debate.getEndAt()).isEqualTo(createdAt.plusDays(7L));
			assertThat(debate.getEndAt()).isNotEqualTo(commentedAt.plusDays(1L));
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
		Long memberId = 1L;
		Debate debate = TestFixtureGenerator.debate(debateId, null, DebateStatus.CLOSED, endAt, 1);

		// when
		when(debateRepository.findDebateByIdForUpdate(debateId)).thenReturn(Optional.of(debate));

		// then
		assertThatThrownBy(() -> debateService.addComment(commentRequest, debateId, memberId))
			.isInstanceOf(BaseException.class)
			.hasMessage("이미 닫힌 토론에 대한 댓글 작성요청입니다. Debate ID: " + debateId);
	}

	@Test
	@DisplayName("토론 댓글 삭제")
	void deleteComment() {
		// given
		String commentContent = "댓글 내용1";
		Long commentId = 2L;
		Long debateId = 1L;
		Long commenterId = 3L;
		Member commenter = TestFixtureGenerator.member(commenterId, "commenter1");
		Debate debate = TestFixtureGenerator.debate(debateId, null, DebateStatus.OPEN, null, 1, null);
		Comment comment = TestFixtureGenerator.comment(commentId, debate, commenter, commentContent, 1);

		when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

		// when
		debateService.deleteComment(commentId, commenterId);

		// then
		verify(commentRepository, times(1)).findById(commentId);
		verify(commentRepository, times(1)).delete(comment);
	}

	@Test
	@DisplayName("토론 댓글 삭제 권한 없음")
	void deleteOtherMemberComment() {
		// given
		String commentContent = "댓글 내용1";
		Long commentId = 2L;
		Long debateId = 1L;
		Long commenterId = 3L;
		Long attackerId = 4L;
		Member commenter = TestFixtureGenerator.member(commenterId, "commenter1");
		Debate debate = TestFixtureGenerator.debate(debateId, null, DebateStatus.OPEN, null, 1, null);
		Comment comment = TestFixtureGenerator.comment(commentId, debate, commenter, commentContent, 1);

		when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

		// when

		// then
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
		Long commentId = 2L;
		Long debateId = 1L;
		Long commenterId = 3L;
		Member commenter = TestFixtureGenerator.member(commenterId, "commenter1");
		Debate debate = TestFixtureGenerator.debate(debateId, null, DebateStatus.OPEN, null, 1, null);
		Comment comment = TestFixtureGenerator.comment(commentId, debate, commenter, originalCommentContent, 1);

		when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

		// when
		debateService.updateComment(commentId, commentRequest, commenterId);

		// then
		// 댓글이 수정되면 댓글의 내용이 변경된다.
		verify(commentRepository, times(1)).findById(commentId);
		assertThat(comment.getContent()).isEqualTo(updatedCommentContent);
	}

	@Test
	@DisplayName("토론 댓글 수정 권한 없음")
	void updateOtherMemberComment() {
		// given
		String originalCommentContent = "댓글 내용1";
		String updatedCommentContent = "수정된 댓글 내용1";
		CommentRequest commentRequest = CommentRequest.of(updatedCommentContent);
		Long commentId = 2L;
		Long debateId = 1L;
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
}
