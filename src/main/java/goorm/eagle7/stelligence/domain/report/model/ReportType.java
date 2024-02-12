package goorm.eagle7.stelligence.domain.report.model;

enum ReportType {

	DOCUMENT(Types.DOCUMENT),
	COMMENT(Types.COMMENT);

	ReportType(String type) {
		if (!this.name().equals(type)) {
			throw new IllegalArgumentException("비정상적인 ReportType의 사용입니다.");
		}
	}

	static final class Types {
		static final String DOCUMENT = "DOCUMENT";
		static final String COMMENT = "COMMENT";

		private Types() {
			throw new IllegalStateException("생성할 수 없는 클래스입니다.");
		}
	}
}
