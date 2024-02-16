package goorm.eagle7.stelligence.common.util;

/**
 * 애플리케이션 사이트의 정보를 가지고 있는 정적 유틸 클래스입니다.ㄴㄴ
 */
public final class Site {

	private Site() {
		throw new AssertionError("Util class cannot be instantiated.");
	}

	private static final String VOTE_URI_TEMPLATE = "/vote-list/%d";
	private static final String DEBATE_URI_TEMPLATE = "/debate-list/%d";
	private static final String DOCUMENT_URI_TEMPLATE = "/stars/%d";
	private static final String MYPAGE_URI_TEMPLATE = "/mypage";

	public static String vote(Long contributeId) {
		return String.format(VOTE_URI_TEMPLATE, contributeId);
	}

	public static String debate(Long debateId) {
		return String.format(DEBATE_URI_TEMPLATE, debateId);
	}

	public static String document(Long documentId) {
		return String.format(DOCUMENT_URI_TEMPLATE, documentId);
	}

	public static String mypage() {
		return MYPAGE_URI_TEMPLATE;
	}

}
