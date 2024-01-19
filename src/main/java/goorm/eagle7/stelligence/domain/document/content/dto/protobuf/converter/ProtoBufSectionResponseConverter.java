package goorm.eagle7.stelligence.domain.document.content.dto.protobuf.converter;

import goorm.eagle7.stelligence.domain.document.content.dto.SectionResponse;
import goorm.eagle7.stelligence.domain.document.content.dto.protobuf.SectionResponseOuterClass;

/**
 * ProtoBufSectionResponseConverter
 * SectionResponse와 SectionResponseOuterClass.SectionResponse 간의 변환을 수행하는 유틸 클래스입니다.
 *
 * 캐싱을 위해 사용됩니다.
 */
public class ProtoBufSectionResponseConverter {

	private ProtoBufSectionResponseConverter() {
	}

	public static SectionResponse toMySectionResponse(
		SectionResponseOuterClass.SectionResponse protoBufSectionResponse) {
		return SectionResponse.of(
			protoBufSectionResponse.getSectionId(),
			protoBufSectionResponse.getRevision(),
			ProtoBufHeadingConverter.toHeading(protoBufSectionResponse.getHeading()),
			protoBufSectionResponse.getTitle(),
			protoBufSectionResponse.getContent()
		);
	}

	public static SectionResponseOuterClass.SectionResponse toProtoBufSectionResponse(
		SectionResponse mySectionResponse) {
		return SectionResponseOuterClass.SectionResponse.newBuilder()
			.setSectionId(mySectionResponse.getSectionId())
			.setRevision(mySectionResponse.getRevision())
			.setHeading(ProtoBufHeadingConverter.toProtoBufHeading(mySectionResponse.getHeading()))
			.setTitle(mySectionResponse.getTitle())
			.setContent(mySectionResponse.getContent())
			.build();
	}
}
