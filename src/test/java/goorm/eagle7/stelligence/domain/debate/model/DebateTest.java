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
}