package goorm.eagle7.stelligence.domain.bookmark;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import goorm.eagle7.stelligence.api.ResponseTemplate;
import goorm.eagle7.stelligence.common.auth.memberinfo.Auth;
import goorm.eagle7.stelligence.common.auth.memberinfo.MemberInfo;
import goorm.eagle7.stelligence.domain.bookmark.dto.BookmarkCreateRequest;
import goorm.eagle7.stelligence.domain.bookmark.dto.BookmarkDeleteRequest;
import goorm.eagle7.stelligence.domain.bookmark.dto.BookmarkSimpleResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookmarks")
public class BookmarkController {

	// private final BookmarkService bookmarkService;

	@Operation(summary = "북마크 목록 조회", description = "로그인한 사용자의 북마크 목록을 조회합니다.")
	@ApiResponse(
		responseCode = "200",
		description = "북마크 목록 조회 성공",
		useReturnTypeSchema = true
	)
	@GetMapping
	public ResponseTemplate<List<BookmarkSimpleResponse>> getBookmarks(
		@Auth MemberInfo memberInfo) {

		// List<BookmarkSimpleResponse> bookmarks = bookmarkService.getBookmarks(memberInfo.getId());
		// return ResponseTemplate.ok(bookmarks);
		return ResponseTemplate.ok();

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

		// bookmarkService.createBookmark(memberInfo.getId(), bookmarkCreateRequest);
		return ResponseTemplate.ok();

	}

	@Operation(summary = "북마크 삭제", description = "원하는 북마크를 삭제합니다.")
	@ApiResponse(
		responseCode = "200",
		description = "북마크 삭제 성공",
		useReturnTypeSchema = true
	)
	@DeleteMapping
	public ResponseTemplate<Void> deleteBookmark(
		@Auth MemberInfo memberInfo,
		@RequestBody BookmarkDeleteRequest bookmarkDeleteRequest) {

		// bookmarkService.delete(memberInfo.getId(), bookmarkDeleteRequest);
		return ResponseTemplate.ok();

	}

}
