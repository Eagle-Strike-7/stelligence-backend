package goorm.eagle7.stelligence.domain.badge;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import goorm.eagle7.stelligence.domain.badge.model.Badge;

@Component
public class BadgeImageUrlBuilder {

	@Value("${server.uri}")
	private String serverUri;

	private static final String BADGE_FOLDER_NAME = "/badges/";

	/**
	 * 뱃지 이미지 URL 생성
	 * @return 뱃지 이미지 URL
	 */
	public String buildBadgeImageUrl(Badge badge) {
		return serverUri
			+ BADGE_FOLDER_NAME
			+ badge.getImgFilename();
	}

}
