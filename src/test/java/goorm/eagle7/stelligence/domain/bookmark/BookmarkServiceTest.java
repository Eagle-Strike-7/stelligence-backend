package goorm.eagle7.stelligence.domain.bookmark;

import static goorm.eagle7.stelligence.config.mockdata.TestFixtureGenerator.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import goorm.eagle7.stelligence.api.exception.BaseException;
import goorm.eagle7.stelligence.domain.bookmark.dto.BookmarkCreateRequest;
import goorm.eagle7.stelligence.domain.bookmark.model.Bookmark;
import goorm.eagle7.stelligence.domain.document.content.DocumentContentRepository;
import goorm.eagle7.stelligence.domain.document.content.model.Document;
import goorm.eagle7.stelligence.domain.member.MemberRepository;
import goorm.eagle7.stelligence.domain.member.model.Member;

@ExtendWith(MockitoExtension.class)
class BookmarkServiceTest {

	@Mock
	private BookmarkRepository bookmarkRepository;
	@Mock
	private MemberRepository memberRepository;
	@Mock
	private DocumentContentRepository documentContentRepository;

	@InjectMocks
	private BookmarkService bookmarkService;

	private Member stdMember;
	private Member testMember;
	private Document stdDocument;
	private Document testDocument;
	private Bookmark bookmark;

	@BeforeEach
	void setUp() {
		stdMember = member(1L, "nickname");
		testMember = member(2L, "nickname2");
		stdDocument = document(1L, stdMember, "title", 1L);
		testDocument = document(2L, stdMember, "title2", 1L);
		bookmark = Bookmark.of(stdMember, stdDocument);
	}

	@Test
	@DisplayName("[정상] 북마크 생성 - createBookmark")
	void createBookmarkTrue() {
		// given

		Long memberId = stdMember.getId();
		Long documentId = stdDocument.getId();

		when(memberRepository.findById(memberId)).thenReturn(Optional.of(stdMember));
		when(documentContentRepository.findById(documentId)).thenReturn(Optional.of(stdDocument));

		// when
		bookmarkService.createBookmark(memberId, BookmarkCreateRequest.from(documentId));

		// then
		verify(memberRepository, times(1)).findById(memberId);

	}

	@Test
	@DisplayName("[예외] 중복 memberId로 북마크 생성 - createBookmark")
	void createBookmarkDuplicatedMemberIdFalse() {
		// given
		// when(memberRepository.findById(1L)).thenReturn(Optional.of(stdMember));
		// when(documentContentRepository.findById(1L)).thenReturn(Optional.of(stdDocument));
		// when(bookmarkRepository.save( bookmark)).thenThrow(DataIntegrityViolationException.class);

		// when
		// bookmarkService.createBookmark(1L, BookmarkCreateRequest.from(1L));

		// then - TODO DB 수준의 예외 처리 확인
		// assertThatThrownBy(() -> bookmarkService.createBookmark(1L, BookmarkCreateRequest.from(1L)))
		// 	.isInstanceOf(DataIntegrityViolationException.class);

	}

	@Test
	@DisplayName("[예외] 중복 documentId로 북마크 생성 - createBookmark")
	void createBookmarkDuplicatedDocumentIdFalse() {
		// given

		// when
		// bookmarkService.createBookmark(2L, BookmarkCreateRequest.from(1L));

		// then

	}

	@Test
	@DisplayName("[정상] 북마크 삭제 - delete")
	void delete() {
		// given

		Long memberId = stdMember.getId();
		Long documentId = stdDocument.getId();

		when(bookmarkRepository.findByMemberIdAndDocumentId(memberId, documentId)).thenReturn(Optional.of(bookmark));

		// when
		bookmarkService.delete(memberId, documentId);

		// then
		verify(bookmarkRepository, times(1)).findByMemberIdAndDocumentId(memberId, documentId);

	}

	@Test
	@DisplayName("[예외] 없는 북마크 삭제 - delete")
	void deleteEx() {

		// given
		Long memberId = stdMember.getId();
		Long documentId = testDocument.getId();
		when(bookmarkRepository.findByMemberIdAndDocumentId(memberId, documentId)).thenReturn(Optional.empty());

		// when

		// then
		assertThatThrownBy(() -> bookmarkService.delete(memberId, documentId))
			.isInstanceOf(BaseException.class);

	}

	@Test
	@DisplayName("[정상] 북마크 목록 조회 - getBookmarks")
	void getBookmarks() {
		// given
		Long memberId = stdMember.getId();
		PageRequest pageRequest = PageRequest.of(0, 10);
		when(bookmarkRepository.findSliceByMemberIdWithPageable(memberId, pageRequest)).thenReturn(mock(Slice.class));

		// when
		bookmarkService.getBookmarks(memberId, pageRequest);

		// then
		verify(bookmarkRepository, times(1)).findSliceByMemberIdWithPageable(memberId, pageRequest);

	}
}