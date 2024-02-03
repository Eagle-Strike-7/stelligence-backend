package goorm.eagle7.stelligence.domain.bookmark;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.api.exception.BaseException;
import goorm.eagle7.stelligence.domain.bookmark.dto.BookmarkCreateRequest;
import goorm.eagle7.stelligence.domain.bookmark.dto.BookmarkPageResponse;
import goorm.eagle7.stelligence.domain.bookmark.dto.BookmarkSimpleResponse;
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

	// TODO : 한 번씩 눌렀을 때 저장, 삭제 - 확인하기
	@Transactional
	public void createBookmark(Long memberId, BookmarkCreateRequest bookmarkCreateRequest) {

		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new BaseException("해당 멤버를 찾을 수 없습니다. MemberId= " + memberId));

		Document document = documentContentRepository.findById(bookmarkCreateRequest.getDocumentId())
			.orElseThrow(() -> new BaseException(
				"해당 문서를 찾을 수 없습니다. DocumentId= " + bookmarkCreateRequest.getDocumentId()));

		Bookmark bookmark = Bookmark.of(member, document);
		bookmarkRepository.save(bookmark);
	}

	/**
	 * <h2>북마크 삭제</h2>
	 * <p> - 로그인한 사용자의 북마크 삭제, 사용자의 북마크 목록에서도 삭제</p>
	 * @param memberId - 로그인한 사용자의 ID
	 * @param documentId - 북마크 삭제할 문서의 ID
	 */
	@Transactional
	public void delete(Long memberId, Long documentId) {

		Bookmark bookmark = bookmarkRepository
			.findByMemberIdAndDocumentId(memberId, documentId)
			.orElseThrow(
				() -> new BaseException("해당 북마크를 찾을 수 없습니다. MemberId= " + memberId + ", DocumentId= " + documentId));
		bookmark.delete(); // member의 bookmark 목록에서 삭제
		bookmarkRepository.delete(bookmark);

	}

	/**
	 * <h2>북마크 목록 조회</h2>
	 * <p> - 로그인한 사용자의 북마크 목록을 페이지네이션을 적용해 조회.</p>
	 * <p> - 더보기로 구현해 Slice 이용(count 쿼리 X)</p>
	 * @param memberId - 로그인한 사용자의 ID
	 * @param pageable - page, size, sort
	 * @return BookmarkPageRespons - List<BookmarkSimpleResponse> bookmarks, boolean hasNext
	 */
	public BookmarkPageResponse getBookmarks(Long memberId, Pageable pageable) {

		Slice<Bookmark> sliceBookmarks = bookmarkRepository.findSliceByMemberIdWithPageable(memberId, pageable);

		return BookmarkPageResponse.from(sliceBookmarks);

	}

}
