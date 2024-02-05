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
	private Member exMember;
	private Document stdDocument;
	private Document exDocument;

	@BeforeEach
	void setUp() {
		stdMember = member(1L, "nickname");
		exMember = member(2L, "nickname2");
		stdDocument = document(1L, stdMember, "title", 1L);
		exDocument = document(2L, stdMember, "title2", 1L);
	}

	@Test
	@DisplayName("[정상] 북마크 생성 - createBookmark")
	void createBookmarkTrue() {

		// given
		Long memberId = stdMember.getId();
		Long documentId = stdDocument.getId();
		BookmarkCreateRequest bookmarkCreateRequest = BookmarkCreateRequest.from(documentId);
		Bookmark bookmark = bookmark(1L, stdMember, stdDocument); // id가 null인 객체

		// member 존재, document 존재, bookmark 존재하지 않아야 save 가능.
		when(memberRepository.findById(memberId)).thenReturn(Optional.of(stdMember));
		when(documentContentRepository.findById(documentId)).thenReturn(Optional.of(stdDocument));
		when(bookmarkRepository.existsByMemberIdAndDocumentId(memberId, documentId)).thenReturn(false);
		when(bookmarkRepository.save(any(Bookmark.class))).thenReturn(bookmark);

		// when
		bookmarkService.createBookmark(memberId, bookmarkCreateRequest);

		// then - 최종 호출 및 최종 수행해야 하는 코드 호출해 검증
		verify(bookmarkRepository, times(1)).save(any(Bookmark.class));

	}

	/**
	 * <h2>[예외] 존재하지 않는 memberId로 북마크 생성 - createBookmark</h2>
	 * <p>결과: BaseException 발생</p>
	 * <p>검증 방식: msg내용 확인, findByID X, save 호출 X</p>
	 */
	@Test
	@DisplayName("[예외] 존재하지 않는 member로 북마크 생성 - createBookmark")
	void createBookmarkNotExistMemberIdFalse() {

		// given
		Long exMemberId = exMember.getId();
		Long documentId = stdDocument.getId();
		BookmarkCreateRequest bookmarkCreateRequest = BookmarkCreateRequest.from(documentId);

		when(memberRepository.findById(exMemberId)).thenReturn(Optional.empty());

		// when

		// then
		assertThatThrownBy(() -> bookmarkService.createBookmark(exMemberId, bookmarkCreateRequest))
			.isInstanceOf(BaseException.class)
			.hasMessage("해당 사용자를 찾을 수 없습니다. MemberId= 2");
		verify(memberRepository, times(1)).findById(any(Long.class));
		verify(bookmarkRepository, never()).save(any(Bookmark.class));

	}

	/**
	 * <h2>[예외] 존재하지 않는 Document로 북마크 생성 - createBookmark</h2>
	 * <p>결과: BaseException 발생</p>
	 * <p>검증 방식: msg내용 확인, save 호출 X</p>
	 */
	@Test
	@DisplayName("[예외] 존재하지 않는 Document로 북마크 생성 - createBookmark")
	void createBookmarkNotExistDocumentIdFalse() {

		// given
		Long memberId = stdMember.getId();
		Long exDocumentId = exDocument.getId();
		BookmarkCreateRequest bookmarkCreateRequest = BookmarkCreateRequest.from(exDocumentId);

		when(memberRepository.findById(memberId)).thenReturn(Optional.of(stdMember));
		when(documentContentRepository.findById(exDocumentId)).thenReturn(Optional.empty());

		// when

		// then
		assertThatThrownBy(() -> bookmarkService.createBookmark(memberId, bookmarkCreateRequest))
			.isInstanceOf(BaseException.class)
			.hasMessage("해당 문서를 찾을 수 없습니다. DocumentId= 2");
		verify(documentContentRepository, times(1)).findById(any(Long.class));
		verify(bookmarkRepository, never()).save(any(Bookmark.class));

	}

	/**
	 * <h2>[예외] 중복 memberId/documentId로 북마크 생성 - createBookmark</h2>
	 * <p>결과: BaseException 발생</p>
	 * <p>검증 방식: msg내용 확인, save 호출 X</p>
	 */
	@Test
	@DisplayName("[예외] 중복 memberId/documentId로 북마크 생성 - createBookmark")
	void createBookmarkDuplicatedMemberIdFalse() {

		// given
		Long exMemberId = exMember.getId();
		Long documentId = stdDocument.getId();
		BookmarkCreateRequest bookmarkCreateRequest = BookmarkCreateRequest.from(documentId);

		when(memberRepository.findById(exMemberId)).thenReturn(Optional.of(exMember));
		when(documentContentRepository.findById(documentId)).thenReturn(Optional.of(stdDocument));
		when(bookmarkRepository.existsByMemberIdAndDocumentId(exMemberId, documentId)).thenReturn(true);

		// when

		// then
		assertThatThrownBy(() -> bookmarkService.createBookmark(exMemberId, bookmarkCreateRequest))
			.isInstanceOf(BaseException.class)
			.hasMessage("이미 북마크한 문서입니다.");
		verify(bookmarkRepository, never()).save(any(Bookmark.class));

	}

	/**
	 * <h2>[정상] 북마크 삭제 - deleteBookmark</h2>
	 * <p>결과: deleteBookmark 호출</p>
	 * <p>검증 방식: delete 호출 횟수</p>
	 */
	@Test
	@DisplayName("[정상] 북마크 삭제 - delete")
	void delete() {
		// given

		Long memberId = stdMember.getId();
		Long documentId = stdDocument.getId();

		// when
		bookmarkService.deleteBookmark(memberId, documentId);

		// then
		verify(bookmarkRepository, times(1)).deleteByMemberIdAndDocumentId(memberId, documentId);

	}

	/**
	 * <h2>[예외] 없는 북마크 삭제 - delete</h2>
	 * <p>결과: deleteBookmark 호출, Ex 발생 X</p>
	 * <p>검증 방식: repository delete 호출 횟수</p>
	 */
	@Test
	@DisplayName("[예외] 없는 북마크 삭제 - delete")
	void deleteEx() {

		// given
		Long memberId = stdMember.getId();
		Long documentId = exDocument.getId();

		// when
		bookmarkService.deleteBookmark(memberId, documentId);

		// then
		verify(bookmarkRepository, times(1)).deleteByMemberIdAndDocumentId(memberId, documentId);

	}

}