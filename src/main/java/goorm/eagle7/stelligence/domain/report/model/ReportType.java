package goorm.eagle7.stelligence.domain.report.model;

/**
 * 엔티티에서 &#064;Inheritance(strategy = InheritanceType.SINGLE_TABLE)를 사용할 경우,
 * 실제 DB 테이블 구조는 하나의 테이블에 모든 하위 엔티티가 함께 정의됩니다.
 *
 * <p>
 * 이 때 &#064;DiscriminatorColumn(name = "column_name")과 같이 부모 테이블에서 자식 엔티티를 식별할 컬럼의 이름을 결정할 수 있고,
 * &#064;DiscriminatorValue(value  = "value1")와 같이 자식 엔티티의 DiscriminatorColumn의 필드명을 결정할 수 있습니다.
 *
 * <p>
 * 이때 &#064;DiscriminatorValue의 value에는 String 값이 들어가야하기 때문에, 일반적인 enum은 포함될 수 없습니다.
 * const 값이 들어가야하기 때문에 enum.name(), enum.toString() 등의 메서드는 허용되지 않습니다.
 * <p>
 * 그래서 &#064;DiscriminatorValue에 enum을 넣기 위한 트릭으로, 내부 중첩 클래스를 두어 const String 값으로 enum을 구분할 수 있도록
 * 하였습니다.
 *
 * <p>
 * 또한 &#064;DiscriminatorValue에서 말고는 사용할 일이 없는 value이기 때문에(enum.name()으로 대체 가능),
 * 필드로 유지하지 않고 생성자 수준에서 적절한 사용인지 검증만 수행합니다
 * <p>
 * Report의 자식 엔티티인 CommentReport와 DocumentReport에서만 사용되는 클래스이므로 package-private으로 접근 수준을 제한합니다.
 */
enum ReportType {

	DOCUMENT(Types.DOCUMENT),
	COMMENT(Types.COMMENT);

	ReportType(String type) {
		if (!this.name().equals(type)) {
			throw new IllegalArgumentException("비정상적인 ReportType의 사용입니다.");
		}
	}

	/**
	 * 각 enum의 name과 동일한 값을 갖는 const를 정의한 내부 중첩 클래스입니다.
	 * <p>
	 * Report의 자식 엔티티인 CommentReport와 DocumentReport에서만 사용되는 클래스이므로 package-private으로 접근 수준을 제한합니다.
	 */
	static final class Types {
		static final String DOCUMENT = "DOCUMENT";
		static final String COMMENT = "COMMENT";

		private Types() {
			throw new IllegalStateException("생성할 수 없는 클래스입니다.");
		}
	}
}
