package goorm.eagle7.stelligence.domain.bookmark;

import static goorm.eagle7.stelligence.config.mockdata.TestFixtureGenerator.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

import goorm.eagle7.stelligence.domain.bookmark.model.Bookmark;
import goorm.eagle7.stelligence.domain.document.content.model.Document;
import goorm.eagle7.stelligence.domain.member.model.Member;

@DataJpaTest
class BookmarkRepositoryTest {

	@Autowired
	private BookmarkRepository bookmarkRepository;

	private Bookmark bookmark1;
	private Bookmark bookmark2;
	private Bookmark bookmark3;
	private Bookmark bookmark4;

	private Member stdMember;
	private Member exMember;
	private Document document;

	@BeforeEach
	void setUp() {

		stdMember = member(1L, "nickname");
		exMember = member(4L, "nickname2");
		document = document(1L, stdMember, "title1", 1L);

		// given - memberId가 1L인 북마크 총 4개
		bookmark1 = bookmarkRepository.findById(1L).get(); // memberId: 1L, documentId: 1L
		bookmark2 = bookmarkRepository.findById(5L).get(); // memberId: 1L, documentId: 2L
		bookmark3 = bookmarkRepository.findById(8L).get(); // memberId: 1L, documentId: 3L
		bookmark4 = bookmarkRepository.findById(10L).get(); // memberId: 1L, documentId: 4L

	}

	/**
	 * <h2>[정상] 존재하는 북마크 존재 여부 조회</h2>
	 * <p> - memberId, documentId를 이용해 북마크 존재 여부 조회</p>
	 * <p> - memberId: 1L, documentId: 1L인 북마크는 존재함</p>
	 * <p>결과: memberId, documentId가 일치하면 true 반환</p>
	 * <p>검증 방식: 반환값 확인</p>
	 */
	@Test
	@DisplayName("[정상] 존재하는 북마크 존재 여부 조회 - existsByMemberIdAndDocumentId")
	void existsByMemberIdAndDocumentId() {

		// given
		Long memberId = stdMember.getId();
		Long documentId = document.getId();

		// when
		boolean exists = bookmarkRepository.existsByMemberIdAndDocumentId(memberId, documentId);

		// then
		assertThat(exists).isTrue();

	}

	/**
	 * <h2>[정상] 존재하지 않는 북마크 존재 여부 조회</h2>
	 * <p> - memberId, documentId를 이용해 북마크 존재 여부 조회</p>
	 * <p> - memberId: 4L, documentId: 1L인 북마크는 존재하지 않음</p>
	 * <p>결과: memberId, documentId가 일치하지 않으면 false 반환</p>
	 * <p>검증 방식: 반환값 확인</p>
	 */
	@Test
	@DisplayName("[정상] 존재하지 않는 북마크 존재 여부 조회 - existsByMemberIdAndDocumentId")
	void existsByMemberIdAndDocumentIdEx() {

		// given
		Long memberId = exMember.getId();
		Long documentId = document.getId();

		// when - {memberId, documentId} == {4L, 1L}인 북마크는 존재하지 않음
		boolean exists = bookmarkRepository.existsByMemberIdAndDocumentId(memberId, documentId);

		// then
		assertThat(exists).isFalse();

	}

	/**
	 * <h2>[정상] 특정 사용자의 북마크 조회, 순서 확인</h2>
	 * <p> - memberId를 이용해 사용자의 북마크 목록을 조회, Slice로 반환</p>
	 * <p> - page, size를 받아 조회</p>
	 * <p>결과: memberId가 1L인 북마크 총 4개 조회, bookmarkId 기준 asc 순서 확인</p>
	 * <p>검증 방식: 반환값 크기, 순서 확인</p>
	 */
	@Test
	@DisplayName("[정상] 특정 사용자의 북마크 조회, 순서 확인 - findSliceByMemberIdWithPageable")
	void findSliceByMemberIdWithPageable() {

		// given

		// when
		Slice<Bookmark> bookmarkSlice = bookmarkRepository.findSliceByMemberIdWithPageable(1L,
			PageRequest.of(0, 10));

		// then
		assertThat(bookmarkSlice).isNotNull();
		assertThat(bookmarkSlice.getContent()).hasSize(4);
		// bookmarkId 기준 순서
		assertThat(bookmarkSlice.getContent()).containsExactly(
			bookmark1, bookmark2, bookmark3, bookmark4
		);

	}

	/**
	 * <h2>[정상] 특정 사용자의 북마크 조회, sort 무시, 순서 확인</h2>
	 * <p> - memberId를 이용해 사용자의 북마크 목록을 조회, Slice로 반환</p>
	 * <p> - page, size, sort를 받아 조회</p>
	 * <p> - sort(document, desc) 무시하고 bookmarkId 기준으로 정렬하는지 확인</p>
	 * <p>결과: memberId가 1L인 북마크 총 4개 조회, bookmarkId 기준 asc 순서 확인</p>
	 * <p>검증 방식: 반환값 크기, 순서 확인</p>
	 */
	@Test
	@DisplayName("[정상] 특정 사용자의 북마크 조회, sort 무시, 순서 확인 - findSliceByMemberIdWithPageable")
	void findSliceByMemberIdWithPageableSort() {

		// given

		// when
		Slice<Bookmark> bookmarkSlice = bookmarkRepository.findSliceByMemberIdWithPageable(1L,
			PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "document")));

		// then
		assertThat(bookmarkSlice).isNotNull();
		assertThat(bookmarkSlice.getContent()).hasSize(4);
		// bookmarkId 기준 순서
		assertThat(bookmarkSlice.getContent()).containsExactly(
			bookmark1, bookmark2, bookmark3, bookmark4
		);

	}

	/**
	 * <h2>[정상] 북마크 단건 조회 - findByMemberIdAndDocumentId</h2>
	 * <p> - memberId, documentId를 이용해 북마크 단건 조회</p>
	 * <p>결과: memberId: 1L, documentId: 1L인 북마크 반환</p>
	 * <p>검증 방식: 기존 저장된 북마크와 같은지 id 비교</p>
	 */
	@Test
	@DisplayName("[정상] 북마크 단건 조회 - findByMemberIdAndDocumentId")
	void findByMemberIdAndDocumentId() {

		// given
		Long memberId = stdMember.getId();
		Long documentId = document.getId();

		// when
		Bookmark bookmark = bookmarkRepository.findByMemberIdAndDocumentId(memberId, documentId).get();

		// then
		assertThat(bookmark).isNotNull();
		assertThat(bookmark.getId()).isEqualTo(1L);

	}

	/**
	 * <h2>[예외] 없는 북마크 단건 조회 - findByMemberIdAndDocumentId</h2>
	 * <p> - memberId, documentId를 이용해 북마크 단건 조회</p>
	 * <p> - memberId: 4L, documentId: 1L인 북마크는 존재하지 않음</p>
	 * <p>결과: Optional.empty() 반환</p>
	 * <p>검증 방식: 반환값 확인</p>
	 */
	@Test
	@DisplayName("[예외] 없는 북마크 단건 조회 - findByMemberIdAndDocumentId")
	void findByMemberIdAndDocumentIdEx() {

		// given
		Long memberId = exMember.getId();
		Long documentId = document.getId();

		// when
		Optional<Bookmark> byMemberIdAndDocumentId = bookmarkRepository.findByMemberIdAndDocumentId(memberId,
			documentId);

		// then
		// null X, Optional.empty O, TODO 객체가 비어있다는 것의 의미 확인하기
		assertThat(byMemberIdAndDocumentId).isEmpty();

	}

	/**
	 * <h2>[정상] 북마크 삭제 - deleteByMemberIdAndDocumentId</h2>
	 * <p> - memberId, documentId를 이용해 북마크 삭제</p>
	 * <p>결과: memberId: 1L, documentId: 1L인 북마크 삭제</p>
	 * <p>검증 방식: 삭제 후 조회해서 없는지 확인</p>
	 */
	@Test
	@DisplayName("[정상] 북마크 삭제 - deleteByMemberIdAndDocumentId")
	void deleteByMemberIdAndDocumentId() {

		// given
		Long memberId = stdMember.getId();
		Long documentId = document.getId();

		// when
		bookmarkRepository.deleteByMemberIdAndDocumentId(memberId, documentId);

		// then
		Optional<Bookmark> none = bookmarkRepository.findByMemberIdAndDocumentId(memberId, documentId);
		assertThat(none).isEmpty();

	}

	/**
	 * <h2>[예외] 없는 북마크 삭제 - deleteByMemberIdAndDocumentId</h2>
	 * <p> - memberId, documentId를 이용해 북마크 삭제</p>
	 * <p> - memberId: 4L, documentId: 1L인 북마크는 존재하지 않음</p>
	 * <p> - delete는 void를 반환해 정상 로직과 존재하지 않는 걸 삭제하는 게 같음.</p>
	 * <p>결과: 삭제 후 조회해서 없는지 확인</p>
	 */
	@Test
	@DisplayName("[예외] 없는 북마크 삭제 - deleteByMemberIdAndDocumentId")
	void deleteByMemberIdAndDocumentIdEx() {

		// given
		Long memberId = exMember.getId();
		Long documentId = document.getId();

		// when
		bookmarkRepository.deleteByMemberIdAndDocumentId(memberId, documentId);

		// then
		Optional<Bookmark> none = bookmarkRepository.findByMemberIdAndDocumentId(memberId, documentId);
		assertThat(none).isEmpty();

	}

}