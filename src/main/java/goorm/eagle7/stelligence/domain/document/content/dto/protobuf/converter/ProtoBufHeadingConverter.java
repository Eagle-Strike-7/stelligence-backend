package goorm.eagle7.stelligence.domain.document.content.dto.protobuf.converter;

import goorm.eagle7.stelligence.domain.document.content.dto.protobuf.HeadingOuterClass;
import goorm.eagle7.stelligence.domain.section.model.Heading;

/**
 * ProtoBufHeadingConverter
 * Heading과 HeadingOuterClass.Heading 간의 변환을 수행하는 유틸 클래스입니다.
 *
 * 캐싱을 위해 사용됩니다.
 */
public class ProtoBufHeadingConverter {

	private ProtoBufHeadingConverter() {
	}

	public static Heading toHeading(HeadingOuterClass.Heading protoBufHeading) {
		return switch (protoBufHeading.getNumber()) {
			case 0 -> Heading.H1;
			case 1 -> Heading.H2;
			case 2 -> Heading.H3;
			case 3 -> Heading.H4;
			case 4 -> Heading.H5;
			case 5 -> Heading.H6;
			default -> throw new IllegalArgumentException("잘못된 Heading 값입니다.");
		};
	}

	public static HeadingOuterClass.Heading toProtoBufHeading(Heading heading) {
		return switch (heading) {
			case H1 -> HeadingOuterClass.Heading.H1;
			case H2 -> HeadingOuterClass.Heading.H2;
			case H3 -> HeadingOuterClass.Heading.H3;
			case H4 -> HeadingOuterClass.Heading.H4;
			case H5 -> HeadingOuterClass.Heading.H5;
			case H6 -> HeadingOuterClass.Heading.H6;
		};
	}
}
