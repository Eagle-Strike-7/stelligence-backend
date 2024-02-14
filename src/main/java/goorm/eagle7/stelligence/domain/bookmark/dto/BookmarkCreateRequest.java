package goorm.eagle7.stelligence.domain.bookmark.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class BookmarkCreateRequest {

	@NotNull(message = "북마크를 추가할 문서의 ID를 입력해주세요.")
	private Long documentId;

	public static BookmarkCreateRequest from(Long documentId) {
		return new BookmarkCreateRequest(documentId);
	}

}
