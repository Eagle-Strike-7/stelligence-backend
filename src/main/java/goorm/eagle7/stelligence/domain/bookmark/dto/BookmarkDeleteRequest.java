package goorm.eagle7.stelligence.domain.bookmark.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class BookmarkDeleteRequest {
	private Long documentId;
}