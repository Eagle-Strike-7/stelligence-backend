package goorm.eagle7.stelligence.domain.bookmark;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.api.exception.BaseException;
import goorm.eagle7.stelligence.domain.bookmark.dto.BookmarkCreateRequest;
import goorm.eagle7.stelligence.domain.bookmark.dto.BookmarkPageResponse;
import goorm.eagle7.stelligence.domain.bookmark.model.Bookmark;
import goorm.eagle7.stelligence.domain.document.content.DocumentContentRepository;
import goorm.eagle7.stelligence.domain.document.content.model.Document;
import goorm.eagle7.stelligence.domain.member.MemberRepository;
import goorm.eagle7.stelligence.domain.member.model.Member;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookmarkService {

	private final BookmarkRepository bookmarkRepository;
	private final MemberRepository memberRepository;
	private final DocumentContentRepository documentContentRepository;

	/**
	 * <h2>북마크 생성</h2>
	 * <p> - 로그인한 사용자의 북마크 생성, 사용자의 북마크 목록에 추가</p>
	 * <p> - {documentId, memberId} 중복이면 error 발생</p>
	 * @param memberId - 로그인한 사용자의 ID
	 * @param bookmarkCreateRequest - 북마크 생성할 문서의 ID
	 * @throws DataIntegrityViolationException - memberId, documentId 중복시
	 */
	@Transactional
	public void createBookmark(Long memberId, BookmarkCreateRequest bookmarkCreateRequest) {

		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new BaseException(String.format(
				"해당 사용자를 찾을 수 없습니다. MemberId= %s", memberId)));

		Document document = documentContentRepository.findById(bookmarkCreateRequest.getDocumentId())
			.orElseThrow(() -> new BaseException(
				String.format("해당 문서를 찾을 수 없습니다. DocumentId= %s", bookmarkCreateRequest.getDocumentId())));

		Bookmark bookmark = Bookmark.of(member, document);
		bookmark.addTo(member); // member의 bookmark 목록에 추가
		bookmarkRepository.save(bookmark);
	}

	/**
	 * <h2>북마크 삭제</h2>
	 * <p> - 로그인한 사용자의 북마크 삭제, 사용자의 북마크 목록에서도 삭제</p>
	 * <p> - memberId, documentId가 없어도 exception X</p>
	 * @param memberId - 로그인한 사용자의 ID
	 * @param documentId - 북마크 삭제할 문서의 ID
	 */
	@Transactional
	public void delete(Long memberId, Long documentId) {

		Bookmark bookmark = bookmarkRepository
			.findByMemberIdAndDocumentId(memberId, documentId)
			.orElseThrow(
				() -> new BaseException(String.format(
					"해당 북마크를 찾을 수 없습니다. MemberId= %s, DocumentId= %s", memberId, documentId)));
		bookmark.delete(); // member의 bookmark 목록에서 삭제
		bookmarkRepository.delete(bookmark);

	}

	/**
	 * <h2>북마크 목록 조회</h2>
	 * <p> - 로그인한 사용자의 북마크 목록을 페이지네이션을 적용해 조회.</p>
	 * <p> - 더보기로 구현해 Slice 이용</p>
	 * @param memberId - 로그인한 사용자의 ID
	 * @param pageable - page, size, sort
	 * @return BookmarkPageResponse - List<BookmarkSimpleResponse> bookmarks, boolean hasNext
	 */
	public BookmarkPageResponse getBookmarks(Long memberId, Pageable pageable) {

		Slice<Bookmark> sliceBookmarks = bookmarkRepository.findSliceByMemberIdWithPageable(memberId, pageable);

		return BookmarkPageResponse.from(sliceBookmarks);

	}

}
