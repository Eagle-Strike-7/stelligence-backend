package goorm.eagle7.stelligence.domain.debate.model;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import goorm.eagle7.stelligence.config.mockdata.TestFixtureGenerator;
import goorm.eagle7.stelligence.domain.member.model.Member;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class DebateTest {

	@Test
	@DisplayName("토론기반 수정요청 작성 권한 검증 테스트")
	void hasPermissionToWriteDrivenContribute() {
		//given
		Member commenter1 = TestFixtureGenerator.member(1L, "댓글작성자1");
		Member commenter2 = TestFixtureGenerator.member(2L, "댓글작성자2");
		Member noCommenter = TestFixtureGenerator.member(3L, "댓글 작성안한 회원");

		Debate debate = TestFixtureGenerator.debate(
			1L, null, DebateStatus.CLOSED, LocalDateTime.now(), 1, LocalDateTime.now());
		Comment comment1 = Comment.createComment("댓글1", debate, commenter1);
		Comment comment2 = Comment.createComment("댓글1", debate, commenter2);

		//when
		boolean commenter1HasPermission = debate.hasPermissionToWriteDrivenContribute(commenter1.getId());
		boolean commenter2HasPermission = debate.hasPermissionToWriteDrivenContribute(commenter2.getId());
		boolean noCommenterHasPermission = debate.hasPermissionToWriteDrivenContribute(noCommenter.getId());
		log.info("댓글작성자 목록: {}", debate.getComments()
			.stream().map(c -> c.getCommenter().getNickname()).toList());

		//then
		assertThat(commenter1HasPermission).isTrue();
		assertThat(commenter2HasPermission).isTrue();
		assertThat(noCommenterHasPermission).isFalse();
	}

	@Test
	@DisplayName("토론중인지를 확인")
	void onDebate() {
		//given
		Debate openDebate = TestFixtureGenerator.debate(
			1L, null, DebateStatus.OPEN, LocalDateTime.now().plusDays(1L), 1, LocalDateTime.now());
		Debate closedDebate = TestFixtureGenerator.debate(
			1L, null, DebateStatus.CLOSED, LocalDateTime.now(), 1, LocalDateTime.now());

		//when

		//then
		assertThat(openDebate.isOnDebate()).isTrue();
		assertThat(closedDebate.isOnDebate()).isFalse();
	}

	@Test
	@DisplayName("토론이 끝난 후 수정요청 대기중인지를 확인")
	void pendingForContribute() {
		//given
		Debate openDebate = TestFixtureGenerator.debate(
			1L, null, DebateStatus.OPEN, LocalDateTime.now().plusDays(1L), 1, LocalDateTime.now());
		Debate closedDebateLongTimeAgo = TestFixtureGenerator.debate(
			1L,
			null,
			DebateStatus.CLOSED,
			LocalDateTime.now().minusMinutes(Debate.DEBATE_PENDING_DURATION_MINUTE).minusMinutes(1L),
			1,
			LocalDateTime.now());
		Debate closedDebateRightNow = TestFixtureGenerator.debate(
			1L, null, DebateStatus.CLOSED, LocalDateTime.now(), 1, LocalDateTime.now());

		//when

		//then

		//열린 토론은 수정 요청 대기중이 아님
		assertThat(openDebate.isPendingForContribute()).isFalse();
		//오래전에 닫힌 토론은 수정요청 대기중이 아님
		assertThat(closedDebateLongTimeAgo.isPendingForContribute()).isFalse();
		//닫힌지 얼마 되지 않은 토론은 수정요청 대기중임
		assertThat(closedDebateRightNow.isPendingForContribute()).isTrue();
	}
}