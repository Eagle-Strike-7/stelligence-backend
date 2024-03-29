package goorm.eagle7.stelligence.domain.notification;

import static goorm.eagle7.stelligence.config.mockdata.TestFixtureGenerator.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import goorm.eagle7.stelligence.api.exception.BaseException;
import goorm.eagle7.stelligence.domain.member.MemberRepository;
import goorm.eagle7.stelligence.domain.member.model.Member;
import goorm.eagle7.stelligence.domain.notification.dto.response.NotificationResponse;
import goorm.eagle7.stelligence.domain.notification.model.Notification;
import goorm.eagle7.stelligence.domain.notification.model.NotificationContent;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

	@Mock
	NotificationRepository notificationRepository;
	@Mock
	MemberRepository memberRepository;
	@InjectMocks
	NotificationService notificationService;

	@Test
	@DisplayName("알림 목록 조회 성공")
	void getNotificationsByMemberId() {

		NotificationContent content = TestNotificationContent.builder()
			.message("message")
			.uri("uri")
			.build();

		Notification noti1 = TestNotification.builder()
			.id(1L)
			.content(content)
			.build();

		Notification noti2 = TestNotification.builder()
			.id(2L)
			.content(content)
			.build();

		Notification noti3 = TestNotification.builder()
			.id(3L)
			.content(content)
			.build();

		List<Notification> notifications = List.of(noti1, noti2, noti3);

		Member member = member(1L, "pete");

		//when
		when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
		when(notificationRepository.findByMemberIdOrderByCreatedAtDesc(1L)).thenReturn(notifications);

		List<NotificationResponse> result = notificationService.getNotificationsByMemberId(1L);

		//then
		assertThat(result).hasSize(3);
	}

	@Test
	@DisplayName("알림 목록 조회 실패 - 회원 없음")
	void getNotificationsByMemberIdFailNoMember() {
		//when
		when(memberRepository.findById(1L)).thenReturn(Optional.empty());

		//then
		assertThatThrownBy(() -> notificationService.getNotificationsByMemberId(1L))
			.isInstanceOf(BaseException.class)
			.hasMessage("존재하지 않는 회원입니다.");
	}

	@Test
	@DisplayName("알림 읽음처리 성공")
	void readNotification() {
		Long memberId = 1L;
		Long notificationId = 2L;

		Notification noti = TestNotification.builder()
			.id(2L)
			.memberId(1L)
			.isRead(false)
			.build();

		//when
		when(memberRepository.findById(memberId)).thenReturn(Optional.of(member(1L, "pete")));
		when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(noti));

		notificationService.readNotification(notificationId, memberId);

		//then
		assertThat(noti.isRead()).isTrue();
	}

	@Test
	@DisplayName("알림 읽음처리 실패 - 사용자 없음")
	void readNotificationFailNoMember() {
		Long memberId = 1L;

		//when
		when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> notificationService.readNotification(1L, memberId))
			.isInstanceOf(BaseException.class)
			.hasMessage("존재하지 않는 회원입니다.");
	}

	@Test
	@DisplayName("알림 읽음처리 실패 - 권한 없음")
	void readNotificationFailNoAuth() {
		Long memberId = 1L;
		Long notificationId = 2L;

		Notification noti = TestNotification.builder()
			.id(2L)
			.memberId(2L)
			.isRead(false)
			.build();

		//when
		when(memberRepository.findById(memberId)).thenReturn(Optional.of(member(1L, "pete")));
		when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(noti));

		assertThatThrownBy(() -> notificationService.readNotification(notificationId, memberId))
			.isInstanceOf(BaseException.class)
			.hasMessage("권한이 없습니다.");
	}

	@Test
	@DisplayName("알림 읽음처리 실패 - 알림 없음")
	void readNotificationFailNoNotification() {
		Long memberId = 1L;
		Long notificationId = 2L;

		//when
		when(memberRepository.findById(memberId)).thenReturn(Optional.of(member(1L, "pete")));
		when(notificationRepository.findById(notificationId)).thenReturn(Optional.empty());

		//then
		assertThatThrownBy(() -> notificationService.readNotification(notificationId, memberId))
			.isInstanceOf(BaseException.class)
			.hasMessage("존재하지 않는 알림입니다.");
	}

	@Test
	@DisplayName("알림 일괄 읽음처리 성공")
	void readAllNotifications() {
		Long memberId = 1L;

		//when
		when(memberRepository.findById(memberId)).thenReturn(Optional.of(member(memberId, "pete")));

		notificationService.readAllNotifications(memberId);

		//then
		verify(notificationRepository).readAllNotificationsByMemberId(memberId);
	}

	@Test
	@DisplayName("알림 일괄 읽음처리 실패 - 사용자 없음")
	void readAllNotificationsFailNoMember() {
		Long memberId = 1L;

		//when
		when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

		//then
		assertThatThrownBy(() -> notificationService.readAllNotifications(memberId))
			.isInstanceOf(BaseException.class)
			.hasMessage("존재하지 않는 회원입니다.");
	}

	@Test
	@DisplayName("알림 삭제 성공")
	void deleteNotification() {
		Long memberId = 1L;
		Long notificationId = 2L;

		Notification noti = TestNotification.builder()
			.id(2L)
			.memberId(1L)
			.build();

		//when
		when(memberRepository.findById(memberId)).thenReturn(Optional.of(member(memberId, "pete")));
		when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(noti));
		notificationService.deleteNotification(notificationId, memberId);

		//then
		verify(notificationRepository).delete(noti);
	}

	@Test
	@DisplayName("알림 삭제 실패 - 사용자 없음")
	void deleteNotificationFailNoMember() {
		Long memberId = 1L;
		Long notificationId = 2L;

		//when
		when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

		//then
		assertThatThrownBy(() -> notificationService.deleteNotification(notificationId, memberId))
			.isInstanceOf(BaseException.class)
			.hasMessage("존재하지 않는 회원입니다.");
	}

	@Test
	@DisplayName("알림 삭제 실패 - 알림 없음")
	void deleteNotificationFailNoNotification() {
		Long memberId = 1L;
		Long notificationId = 2L;

		//when
		when(memberRepository.findById(memberId)).thenReturn(Optional.of(member(memberId, "pete")));
		when(notificationRepository.findById(notificationId)).thenReturn(Optional.empty());

		//then
		assertThatThrownBy(() -> notificationService.deleteNotification(notificationId, memberId))
			.isInstanceOf(BaseException.class)
			.hasMessage("존재하지 않는 알림입니다.");
	}

	@Test
	@DisplayName("알림 삭제 실패 - 권한 없음")
	void deleteNotificationFailNoAuth() {
		Long memberId = 1L;
		Long notificationId = 2L;

		Notification noti = TestNotification.builder()
			.id(2L)
			.memberId(2L)
			.build();

		//when
		when(memberRepository.findById(memberId)).thenReturn(Optional.of(member(memberId, "pete")));
		when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(noti));

		//then
		assertThatThrownBy(() -> notificationService.deleteNotification(notificationId, memberId))
			.isInstanceOf(BaseException.class)
			.hasMessage("권한이 없습니다.");
	}

	@Test
	@DisplayName("알림 일괄 삭제 성공")
	void deleteAllNotifications() {
		Long memberId = 1L;

		//when
		when(memberRepository.findById(memberId)).thenReturn(Optional.of(member(memberId, "pete")));

		notificationService.deleteAllNotifications(memberId);

		//then
		verify(notificationRepository).deleteAllByMemberId(memberId);
	}

	@Test
	@DisplayName("알림 일괄 삭제 실패 - 사용자 없음")
	void deleteAllNotificationsFailNoMember() {
		Long memberId = 1L;

		//when
		when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

		//then
		assertThatThrownBy(() -> notificationService.deleteAllNotifications(memberId))
			.isInstanceOf(BaseException.class)
			.hasMessage("존재하지 않는 회원입니다.");
	}
}