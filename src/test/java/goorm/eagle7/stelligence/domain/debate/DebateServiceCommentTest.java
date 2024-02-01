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

		MockedStatic<LocalDateTime> mockedLocalDateTime = Mockito.mockStatic(LocalDateTime.class);
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

		MockedStatic<LocalDateTime> mockedLocalDateTime = Mockito.mockStatic(LocalDateTime.class);
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
}
