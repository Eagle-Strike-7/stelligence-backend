package goorm.eagle7.stelligence.domain.bookmark;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import goorm.eagle7.stelligence.api.ResponseTemplate;
import goorm.eagle7.stelligence.common.auth.memberinfo.Auth;
import goorm.eagle7.stelligence.common.auth.memberinfo.MemberInfo;
import goorm.eagle7.stelligence.domain.bookmark.dto.BookmarkCreateRequest;
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
  			- 로그인한 사용자의 북마크 목록을 페이지네이션을 적용하여 조회합니다.
  			- 기본값: page = 0, size = 10, sort = "id"(bookmarkId),ASC(오름차순)
  			- 예시: /api/bookmarks?page=0&sort=id,desc&size=8
  			- 이때, sort 시 필드명과 정렬 방식을 콤마로 구분하여 입력합니다. 대소문자는 관계 없습니다. 정렬방식은 asc(오름차순) 또는 desc(내림차순)을 입력합니다.
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
		@PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) // sort는 entity의 필드명 기준
		Pageable pageable) {

		BookmarkPageResponse bookmarksResponse = bookmarkService.getBookmarks(memberInfo.getId(), pageable);
		return ResponseTemplate.ok(bookmarksResponse);

	}

	@Operation(summary = "북마크 생성", description = "로그인한 사용자의 북마크를 생성합니다.")
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

	@Operation(summary = "북마크 삭제", description = "documentId로 원하는 북마크를 삭제합니다.")
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

		bookmarkService.delete(memberInfo.getId(), documentId);
		return ResponseTemplate.ok();

	}

}
