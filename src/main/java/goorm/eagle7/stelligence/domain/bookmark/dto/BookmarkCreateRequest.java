package goorm.eagle7.stelligence.domain.bookmark.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class BookmarkCreateRequest {
	private Long documentId;

	public static BookmarkCreateRequest from(Long documentId) {
		return new BookmarkCreateRequest(documentId);
	}

}
