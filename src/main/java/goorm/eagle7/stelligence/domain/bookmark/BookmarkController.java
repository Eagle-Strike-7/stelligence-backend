package goorm.eagle7.stelligence.domain.bookmark;

import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import goorm.eagle7.stelligence.api.ResponseTemplate;
import goorm.eagle7.stelligence.api.exception.BaseException;
import goorm.eagle7.stelligence.common.auth.memberinfo.Auth;
import goorm.eagle7.stelligence.common.auth.memberinfo.MemberInfo;
import goorm.eagle7.stelligence.domain.bookmark.dto.BookmarkCreateRequest;
import goorm.eagle7.stelligence.domain.bookmark.dto.BookmarkOneResponse;
import goorm.eagle7.stelligence.domain.bookmark.dto.BookmarkPageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookmarks")
public class BookmarkController {

	private final BookmarkService bookmarkService;

	@Operation(summary = "북마크 목록 조회",
		description = """
						- 로그인한 사용자의 북마크 목록을 페이지네이션을 적용해 bookmarkId 기준 오름차순으로 조회합니다.
						- 기본값: page = 0, size = 10
						- 예시: /api/bookmarks?page=1&size=8
			"""
	)
	@ApiResponse(
		responseCode = "200",
		description = "북마크 목록 조회 성공",
		useReturnTypeSchema = true
	)
	@GetMapping
	public ResponseTemplate<BookmarkPageResponse> getBookmarks(
		@Auth MemberInfo memberInfo,
		@Parameter(description = "페이지 번호", example = "0")
		@RequestParam(required = false, defaultValue = "0") int page,
		@Parameter(description = "페이지 크기", example = "10")
		@RequestParam(required = false, defaultValue = "10") int size) {

		BookmarkPageResponse bookmarksResponse = bookmarkService.getBookmarks(
			memberInfo.getId(),
			PageRequest.of(page, size));
		return ResponseTemplate.ok(bookmarksResponse);

	}

	@Operation(summary = "북마크 단건 조회",
		description = """
						- 로그인한 사용자가 문서를 확인할 때, 북마크했는지 여부를 조회합니다.
						- 로그인하지 않으면 400 에러를 반환합니다.
			"""
	)
	@ApiResponse(
		responseCode = "200",
		description = "북마크 단건 조회 성공",
		useReturnTypeSchema = true
	)
	@GetMapping("/marked")
	public ResponseTemplate<BookmarkOneResponse> getBookmark(
		@Auth MemberInfo memberInfo,
		@Parameter(description = "북마크를 조회할 문서의 documentId를 입력합니다.", example = "1")
		@RequestParam Long documentId) {

		if (memberInfo == null) {
			throw new BaseException("로그인이 필요합니다.");
		}

		BookmarkOneResponse bookmarkOneResponse = bookmarkService.getBookmark(memberInfo.getId(), documentId);
		return ResponseTemplate.ok(bookmarkOneResponse);

	}

	@Operation(summary = "북마크 생성", description =
		"로그인한 사용자의 북마크를 생성합니다."
			+ "이미 북마크한 문서를 다시 북마크하려고 하면 실패합니다.")
	@ApiResponse(
		responseCode = "200",
		description = "북마크 생성 성공",
		useReturnTypeSchema = true
	)
	@PostMapping
	public ResponseTemplate<Void> createBookmark(
		@Auth MemberInfo memberInfo,
		@RequestBody BookmarkCreateRequest bookmarkCreateRequest) {

		bookmarkService.createBookmark(
			memberInfo.getId(), bookmarkCreateRequest);
		return ResponseTemplate.ok();

	}

	@Operation(summary = "북마크 삭제", description =
		"documentId로 원하는 북마크를 삭제합니다."
			+ "존재하지 않는 북마크에 대한 삭제를 요청해도{memberId, documentId}가 없어도 성공입니다."
	)
	@ApiResponse(
		responseCode = "200",
		description = "북마크 삭제 성공",
		useReturnTypeSchema = true
	)
	@DeleteMapping
	public ResponseTemplate<Void> deleteBookmark(
		@Auth MemberInfo memberInfo,
		@Parameter(description = "삭제할 북마크의 documentId를 입력합니다.", example = "1")
		@RequestParam Long documentId) {

		bookmarkService.deleteBookmark(memberInfo.getId(), documentId);
		return ResponseTemplate.ok();

	}

}
