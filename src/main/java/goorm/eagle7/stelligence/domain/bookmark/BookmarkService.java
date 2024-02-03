package goorm.eagle7.stelligence.domain.bookmark;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

	@Transactional
	public void delete(Long memberId, Long bookmarkId) {

		Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
			.orElseThrow(() -> new BaseException("해당 북마크를 찾을 수 없습니다. BookmarkId= " + bookmarkId));

		// 다른 member의 bookmark를 삭제하려고 시도하는 경우, BaseException
		validatePermissionForDelete(memberId, bookmark);

		bookmarkRepository.delete(bookmark);

	}

	private void validatePermissionForDelete(Long memberId, Bookmark bookmark) {
		Long bookmarkId = bookmark.getId();
		if (!bookmark.isSameMember(memberId)) {
			throw new BaseException("해당 북마크에 대한 권한이 없습니다. BookmarkId= " + bookmarkId);
		}
	}

	public List<BookmarkSimpleResponse> getBookmarks(Long memberId) {

		// TODO : 페이징 처리, totalPage, isLast 등 필요한 정보 확인하기.
		// 페이징은 적절히 나눠서 가져 오는데, 끝을 아는 게 중요하다.
		// 데이터를 효율적으로 가져오기 위함이다. 그러므로 페이징을 하지 않는다면, 데이터를 모두 가져오는 것이다.
		// slice == count 쿼리를 날리지 않음, size+1로 조회해서 다음 페이지가 있는지 확인, hasNext, isFirst, isLast 등 확인 - 버튼 비활성화 가능.

		return bookmarkRepository.findByMemberId(memberId)
			.stream()
			.map(BookmarkSimpleResponse::from)
			.toList();

	}

}
