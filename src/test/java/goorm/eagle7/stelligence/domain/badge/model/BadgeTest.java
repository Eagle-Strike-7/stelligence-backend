package goorm.eagle7.stelligence.domain.badge.model;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BadgeTest {

	@Test
	@DisplayName("[성공] - 이벤트 카테고리와 카운트로 뱃지 찾기 - WRITING, 1")
	void findByEventCategoryAndCountWriting1Success() {

		BadgeCategory badgeCategory = BadgeCategory.WRITING;
		long count = 1;
		Badge badge = Badge.findByEventCategoryAndCount(badgeCategory, count);

		assertThat(badge.getTitle()).isEqualTo("우주 여행자");
	}

	@Test
	@DisplayName("[성공] - 이벤트 카테고리와 카운트로 뱃지 찾기 - WRITING, 10")
	void findByEventCategoryAndCountWriting10Success() {

		BadgeCategory badgeCategory = BadgeCategory.WRITING;
		long count = 10;
		Badge badge = Badge.findByEventCategoryAndCount(badgeCategory, count);

		assertThat(badge.getTitle()).isEqualTo("화성 탐사 완료");
	}

	@Test
	@DisplayName("[실패] - 이벤트 카테고리와 카운트로 뱃지 찾기 - WRITING, 8")
	void findByEventCategoryAndCountWriting8Null() {

		BadgeCategory badgeCategory = BadgeCategory.WRITING;
		long count = 8;
		Badge badge = Badge.findByEventCategoryAndCount(badgeCategory, count);

		assertThat(badge).isNull();
	}

	@Test
	@DisplayName("[성공] - 이벤트 카테고리와 카운트로 뱃지 찾기 - CONTRIBUTE_ALL, 5")
	void findByEventCategoryAndCountContributeAll5Success() {

		BadgeCategory badgeCategory = BadgeCategory.CONTRIBUTE_ALL;
		long count = 5;
		Badge badge = Badge.findByEventCategoryAndCount(badgeCategory, count);

		assertThat(badge.getTitle()).isEqualTo("금성 탐사 완료");

	}

	@Test
	@DisplayName("[성공] - 이벤트 카테고리와 카운트로 뱃지 찾기 - CONTRIBUTE_MERGED, 5")
	void findByEventCategoryAndCountContributeMerged5Success() {

		BadgeCategory badgeCategory = BadgeCategory.CONTRIBUTE_MERGED;
		long count = 5;
		Badge badge = Badge.findByEventCategoryAndCount(badgeCategory, count);

		assertThat(badge.getTitle()).isEqualTo("토성 탐사 완료");

	}

	@Test
	@DisplayName("[성공] - 이벤트 카테고리와 카운트로 뱃지 찾기 - CONTRIBUTE_REJECTED, 100")
	void findByEventCategoryAndCountContributeRejected100Success() {

		BadgeCategory badgeCategory = BadgeCategory.CONTRIBUTE_REJECTED;
		long count = 100;
		Badge badge = Badge.findByEventCategoryAndCount(badgeCategory, count);

		assertThat(badge.getTitle()).isEqualTo("블랙홀");

	}

	@Test
	@DisplayName("[실패] - 이벤트 카테고리와 카운트로 뱃지 찾기 - CONTRIBUTE_REJECTED, 99")
	void findByEventCategoryAndCountContributeRejected99Null() {

		BadgeCategory badgeCategory = BadgeCategory.CONTRIBUTE_REJECTED;
		long count = 99;
		Badge badge = Badge.findByEventCategoryAndCount(badgeCategory, count);

		assertThat(badge).isNull();

	}

	@Test
	@DisplayName("[실패] - 이벤트 카테고리와 카운트로 뱃지 찾기 - CONTRIBUTE_REJECTED, 101")
	void findByEventCategoryAndCountContributeRejected101Null() {

		BadgeCategory badgeCategory = BadgeCategory.CONTRIBUTE_REJECTED;
		long count = 101;
		Badge badge = Badge.findByEventCategoryAndCount(badgeCategory, count);

		assertThat(badge).isNull();

	}

	@Test
	@DisplayName("[성공] - 이벤트 카테고리와 카운트로 뱃지 찾기 - MEMBER_JOIN, 1")
	void findByEventCategoryAndCountMemberJoin1Success() {

		BadgeCategory badgeCategory = BadgeCategory.MEMBER_JOIN;
		long count = 1;
		Badge badge = Badge.findByEventCategoryAndCount(badgeCategory, count);

		assertThat(badge.getTitle()).isEqualTo("우주 새싹");

	}

	@Test
	@DisplayName("[성공] - 이벤트 카테고리와 카운트로 뱃지 찾기 - REPORT, 10")
	void findByEventCategoryAndCountReport10Success() {

		BadgeCategory badgeCategory = BadgeCategory.REPORT;
		long count = 10;
		Badge badge = Badge.findByEventCategoryAndCount(badgeCategory, count);

		assertThat(badge.getTitle()).isEqualTo("우주 방범대");

	}

	@Test
	@DisplayName("[성공] - filename 가져오기 - ASTRONAUT")
	void getImgFilename() {

		Badge badge = Badge.ASTRONAUT;
		String imgFilename = badge.getImgFilename();

		assertThat(imgFilename).isEqualTo("/badges/astronaut.png");

	}

}